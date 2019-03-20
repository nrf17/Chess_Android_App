package game.chess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.chess.Bishop;
import model.chess.ChessGame;
import model.chess.Queen;
import model.chess.Rook;

public class GameList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private GameListAdapter savedGameAdapter;
    private static final String filename = "data";
    List<SavedChessGame> games;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        loadData();

        savedGameAdapter = new GameListAdapter(games);
        recyclerView.setAdapter(savedGameAdapter);
        recyclerView.addOnItemTouchListener(
                new GameListListener(this, recyclerView, new GameListListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("GAME", savedGameAdapter.getGame(position));
                        Intent intent = new Intent(GameList.this, PlayBack.class);
                        intent.putExtras(bundle);
                        startActivityForResult(intent, 0);
                    }
                })
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameList.this, PlayGame.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            SavedChessGame newGame = (SavedChessGame) bundle.getSerializable("GAME");
            saveData(newGame);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_game_list, menu);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.sort, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.sort) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_sort_game_list)
                    .setItems(R.array.sort_games_options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    sortByDate();
                                    savedGameAdapter.notifyDataSetChanged();
                                    break;
                                case 1:
                                    sortByName();
                                    break;
                            }
                            savedGameAdapter.notifyDataSetChanged();
                        }
                    });
            builder.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void loadData(){
        try{
            FileInputStream fileIn = this.openFileInput(filename);
            ObjectInputStream objIn = new ObjectInputStream(fileIn);
            games = (ArrayList<SavedChessGame>) objIn.readObject();
            if (games == null){
                games = new ArrayList<SavedChessGame>();
            }
            objIn.close();
            fileIn.close();
        }
        catch(FileNotFoundException e){
            games = new ArrayList<SavedChessGame>();
        catch(IOException e){ e.printStackTrace(); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }

    }



    public void saveData(SavedChessGame newGame){
        FileOutputStream outputStream;
        savedGameAdapter.addGame(0, newGame);

        try{
            outputStream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(outputStream);
            oos.writeObject(savedGameAdapter.getGames());
            outputStream.close();
            oos.close();
        }
        catch(IOException e){ e.printStackTrace(); }


    }


    public void sortByName(){
        if (savedGameAdapter.getGames().size() > 0) {
            Collections.sort(savedGameAdapter.getGames(), new Comparator<SavedChessGame>() {
                @Override
                public int compare(final SavedChessGame object1, final SavedChessGame object2) {
                    if((object1.getName() == null) && (object2.getName() == null))
                        return 0;
                    if(object1.getName() == null)
                        return -1;
                    if(object2.getName() == null)
                        return 1;
                    return object1.getName().compareTo(object2.getName());
                }
            });
        }
    }


    public void sortByDate(){
        if (savedGameAdapter.getGames().size() > 0) {
            Collections.sort(savedGameAdapter.getGames(), new Comparator<SavedChessGame>() {
                @Override
                public int compare(final SavedChessGame object1, final SavedChessGame object2) {
                    return object2.getTime().compareTo(object1.getTime());
                }
            });
        }
    }

}