package lars.benedetto.com.wikimeup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import im.delight.android.webview.AdvancedWebView;

public class ReadArticleActivity extends AppCompatActivity implements AdvancedWebView.Listener {
    private AdvancedWebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        ReadArticleActivity context = this;
        final Voice voice = new Voice(context);

        final Button buttonStop = (Button) findViewById(R.id.buttonStop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voice.stop();
            }
        });
        webView = (AdvancedWebView) findViewById(R.id.webview);
        webView.setListener(this, this);
        final RandomWikipediaArticleTask task = new RandomWikipediaArticleTask(this);
        task.setOnPostExecuteRunnable(new Runnable() {
            @Override
            public void run() {
                voice.speak(task.getArticle());
                webView.loadUrl(task.getLink());
            }
        });
        task.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        webView.onDestroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        webView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (!webView.onBackPressed()) { return; }
        super.onBackPressed();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) { }

    @Override
    public void onPageFinished(String url) { }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) { }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) { }

    @Override
    public void onExternalPageRequest(String url) { }

}
