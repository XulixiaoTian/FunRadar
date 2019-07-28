package com.funradar;

import static com.funradar.DBUtils.upsertUser;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.funradar.entity.User;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

  private int RC_SIGN_IN = 9001;
  private GoogleSignInClient mGoogleSignInClient;
  private SignInButton googleSignInButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    MobileAds.initialize(this,
        "ca-app-pub-3940256099942544~3347511713");

    AdView mAdView = findViewById(R.id.adView_main);
    AdRequest adRequest = new AdRequest.Builder().build();
    mAdView.loadAd(adRequest);

    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestId()
        .requestProfile()
        .build();

    // Build a GoogleSignInClient with the options specified by gso.
    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    // Set the dimensions of the sign-in button.
    googleSignInButton = findViewById(R.id.sign_in_button);
    googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

    googleSignInButton.setOnClickListener(v -> {
      Log.w("ClickListner", "button pressed");
      signIn();
    });

  }

  private void signIn() {
    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
    startActivityForResult(signInIntent, RC_SIGN_IN);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
    if (requestCode == RC_SIGN_IN) {
      // The Task returned from this call is always completed, no need to attach
      // a listener.
      Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
      handleSignInResult(task);
    }
  }

  private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
    try {
      GoogleSignInAccount account = completedTask.getResult(ApiException.class);
      getUser(account);
      //Signed in successfully, show authenticated UI
      startActivity(new Intent(MainActivity.this, EventsActivity.class));
    } catch (ApiException e) {
      // The ApiException status code indicates the detailed failure reason
      //PLease refer the GoogleSignInStatusCode class reference for more information.
      Log.w("Google Sign in Error", "signInResult:failed code" + e.getStatusCode());
      Toast.makeText(MainActivity.this, "Failed to sign in!", Toast.LENGTH_LONG).show();
    }

  }

  @Override
  protected void onStart() {
    //Check for existing Google Sign in account ,if the user is already signed in
    // the GoogleSignInAccount will be non-null
    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    if (account != null) {
      getUser(account);
      startActivity(new Intent(MainActivity.this, EventsActivity.class));
    }
    super.onStart();
  }

  private void getUser(GoogleSignInAccount account) {
    User user = User.builder().setUserId(account.getId())
        .setName(account.getGivenName() + " " + account.getFamilyName())
        .setFirstName(account.getGivenName())
        .setLastName(account.getFamilyName()).setEmail(account.getEmail())
        .setPictureUrl(account.getPhotoUrl().toString()).build();
    Util.user = user;
    upsertUser(user);
    Log.w("Google User Login", "Logged in user " + user);
    Toast.makeText(MainActivity.this, "Welcome, " + user.name(), Toast.LENGTH_LONG).show();
  }
}
