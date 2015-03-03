package io.sweers.barber.sample;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                new AlertDialog.Builder(this)
                        .setTitle("Barber")
                        .setMessage(Html.fromHtml(getString(R.string.about_body)))
                        .setPositiveButton("Done", null)
                        .create().show();
                return true;
            case R.id.action_repo:
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://github.com/hzsweers/barber"));
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
