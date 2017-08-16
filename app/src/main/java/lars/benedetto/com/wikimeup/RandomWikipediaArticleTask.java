package lars.benedetto.com.wikimeup;


import android.app.Activity;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

class RandomWikipediaArticleTask extends AsyncTask<Void, Void, Void> {

    private Activity context;
    private String article;
    private Runnable runnable;
    private String link;

    RandomWikipediaArticleTask(Activity context) {
        this.context = context;
    }

    void setOnPostExecuteRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    private static ArrayList<String> readInput(InputStream is) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        ArrayList<String> articles = new ArrayList<>();
        try {
            while ((line = br.readLine()) != null) {
                articles.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return articles;
    }

    private static String getText(String link) {
        try {
            Document doc = Jsoup.connect(link)
                    .data("query", "Java")
                    .userAgent("Mozilla")
                    .cookie("auth", "token")
                    .timeout(3000)
                    .post();
            StringBuilder sb = new StringBuilder();
            sb.append(doc.select("h1").text()).append(". ");
            for (Element p : doc.select("p")) {
                sb.append(p.text()).append(" ");
            }
            return sb.toString().replaceAll("\\[\\d+]", "").replaceAll("\\(.*?\\)", "");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Could not load article";
    }

    @Override
    protected Void doInBackground(Void... voids) {
        int fileID = context.getResources().getIdentifier("links", "raw", context.getPackageName());
        ArrayList<String> articles = readInput(context.getResources().openRawResource(fileID));
        int i = new Random().nextInt(articles.size());
        link = articles.get(i);
        article = getText(link);
        context.runOnUiThread(runnable);
        return null;
    }

    String getLink() {
        return link;
    }

    String getArticle() {
        return article;
    }
}
