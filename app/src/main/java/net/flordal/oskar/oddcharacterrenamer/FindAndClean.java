package net.flordal.oskar.oddcharacterrenamer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


public class FindAndClean extends Activity {

    ListView lv;
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems=new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_and_clean);

        // display the list and what will be replaced
        adapter=new ArrayAdapter(this,
                android.R.layout.simple_list_item_2,
                android.R.id.text1,
                listItems) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                text1.setText(listItems.get(position));
                text2.setText("Renamed to " + replace(listItems.get(position)));
                text2.setGravity(0x00000005);
                return view;
        }};

        lv = (ListView) findViewById(R.id.listView);
        lv.setAdapter(adapter);

        findFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.find_and_clean, menu);
        return true;
    }

    // change all the illegal chars to a legal char
    private String replace(String str) {
        return str.replaceAll(illegalChars, "_");
    }

    private void addItems(String str) {
        listItems.add(str);
        adapter.notifyDataSetChanged();
    }

    private void removeItems(int num) {
        listItems.remove(num);
        adapter.notifyDataSetChanged();
    }

    String illegalChars = "[/:<>\\|\"]";

    // find all interesting files
    private void findFiles() {
        File rootsd = Environment.getExternalStorageDirectory();
        File dir = new File(rootsd.getAbsolutePath() + "/DCIM/Camera/");

        // get the camera directory
        String[] files = dir.list();

        // Go through each file and stort out the ones with strange chars
        for (String s : files) {
            if (s.matches(".*"+illegalChars+".*")) {
                addItems(s);
            }
        }
    }

    // clean all the files in the list
    public void cleanFiles(View v) {
        File rootsd = Environment.getExternalStorageDirectory();

        while (listItems.size() != 0) {
            String s = listItems.get(0);

            // remove it from list to indicate that we are done processing it
            removeItems(0);

            File f = new File(rootsd.getAbsolutePath() + "/DCIM/Camera/" + s);
            String newName = replace(s);

            //make sure there is not alredy a file with the same name
            File newFile = new File(rootsd.getAbsolutePath() + "/DCIM/Camera/" + newName);

            if (newFile.exists()) {
                // fail
                Toast toast = Toast.makeText(this, "File " + rootsd.getAbsolutePath() + "/DCIM/Camera/" + newName + "already exists, to avoid data loss this has to be fixed manually I am afraid", Toast.LENGTH_SHORT);
                toast.show();

                continue;
            }

            f.renameTo(newFile);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
