package rdm.niazicab;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class ContactUs extends AppCompatActivity {


    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(ContactUs.this ,R.color.colorGreen)));

       bar = (ProgressBar) findViewById(R.id.progress_bar);
        WebView webView = (WebView) findViewById(R.id.webview);

        webView.setWebViewClient(new WebViewClient());

        //webView.loadUrl("https://www.ranglerz.com/contact.php");

        bar.setVisibility(View.VISIBLE);

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.i("TAG", "Processing webview url click...");
                view.loadUrl(url);
                return true;
            }

            public void onPageFinished(WebView view, String url) {
                Log.i("TAG", "Finished loading URL: " + url);
             bar.setVisibility(View.GONE);
            }

        });
        webView.loadUrl("https://www.ranglerz.com/contact.php");
    }
}
