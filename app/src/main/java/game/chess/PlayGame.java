package game.chess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import model.chess.Bishop;
import model.chess.ChessGame;
import model.chess.ChessPiece;
import model.chess.Knight;
import model.chess.Queen;
import model.chess.Rook;

public class PlayGame extends AppCompatActivity {
    private ChessGameNode head;
    private TextView currentTeam;
    private GridView grid;
    private ChessPieceAdapter chessPieceAdapter;
    private boolean undoAllowed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_game);

        currentTeam = (TextView) findViewById(R.id.currentTeam);
        currentTeam.setText(R.string.current_team_prompt_white);

        populateBoard();
        undoAllowed = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_play_game, menu);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.undo, null);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, Color.WHITE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.undo) {
            if (head.getPrev() == null)
                return false;

            if (!undoAllowed) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(R.string.dialog_invalid_undo_main);
                builder.setMessage(R.string.dialog_invalid_undo_secondary);

                builder.setPositiveButton(R.string.ok, null);

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }

            head = head.getPrev();
            chessPieceAdapter.setNode(head);
            undoAllowed = false;
            refreshBoard();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void populateBoard() {
        grid = (GridView) findViewById(R.id.board);

        head = new ChessGameNode(new ChessGame());
        chessPieceAdapter = new ChessPieceAdapter(this, head);
        grid.setAdapter(chessPieceAdapter);

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            ChessPiece selectedPiece = null;
            int currPos = -1;

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                //attempting to move piece to an open spot
                if (grid.getAdapter().getItem(position) == null) {
                    if (selectedPiece != null) {
                        move(currPos, position);
                        String oldColor = getOldColor(selectedPiece.getRow(), selectedPiece.getCol());
                        grid.getChildAt(currPos).setBackgroundColor(Color.parseColor(oldColor));
                        selectedPiece = null;
                        currPos = -1;
                    }
                }

                //attempting to either select piece to move, or trying to capture another piece
                else {

                    if (selectedPiece != null) {
                        ChessPiece toCap = (ChessPiece) grid.getAdapter().getItem(position);

                        if (selectedPiece.getTeam() != toCap.getTeam()) { //trying to capture enemy piece
                            move(currPos, position);
                            String oldColor = getOldColor(selectedPiece.getRow(), selectedPiece.getCol());
                            grid.getChildAt(currPos).setBackgroundColor(Color.parseColor(oldColor));
                            selectedPiece = null;
                            currPos = -1;
                        }
                        else{ //trying to select a different piece to move
                            if (currPos > -1) {
                                String oldColor = getOldColor(selectedPiece.getRow(), selectedPiece.getCol());
                                grid.getChildAt(currPos).setBackgroundColor(Color.parseColor(oldColor));
                                selectedPiece = null;
                                currPos = -1;
                            }
                            currPos = position;
                            selectedPiece = (ChessPiece) grid.getAdapter().getItem(position);
                            grid.getChildAt(position).setBackgroundColor(Color.parseColor("#004ba0"));
                        }

                    }

                    //selecting 1st piece to move
                    else {
                        currPos = position;
                        selectedPiece = (ChessPiece) grid.getAdapter().getItem(position);
                        grid.getChildAt(position).setBackgroundColor(Color.parseColor("#004ba0"));
                    }
                }
            }
        });

    }

    private boolean move(int firstIndex, int secondIndex) {
        ChessGame newGame = head.getGame().clone();
        ChessGameNode newNode = new ChessGameNode(newGame, head);

        if (!newGame.move(firstIndex, secondIndex)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_invalid_move_main);
            builder.setMessage(R.string.dialog_invalid_move_secondary);

            builder.setPositiveButton(R.string.ok, null);

            AlertDialog dialog = builder.create();
            dialog.show();
            return false;
        }

        head.setNext(newNode);
        head = newNode;
        chessPieceAdapter.setNode(head);

        undoAllowed = true;
        refreshBoard();
        pawnPromo();
        return true;
    }

    private void saveGameOption() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.save_game_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(mView);

        final EditText inputText = (EditText) mView.findViewById(R.id.title);

        builder.setCancelable(false)
                .setPositiveButton("Save Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        char winner;
                        Intent intent = new Intent();
                        SavedChessGame savedGame;
                        String titleInput = inputText.getText().toString();

                        if (head.getGame().getCurrentTeam() == 'w')
                            winner = 'b';
                        else
                            winner = 'w';

                        if (!titleInput.isEmpty()) {
                            savedGame = new SavedChessGame(head, Calendar.getInstance(), titleInput);
                        } else {
                            savedGame = new SavedChessGame(head, Calendar.getInstance());
                        }

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("GAME", savedGame);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .setNegativeButton("Discard Game", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        finish();
                    }
                });

        AlertDialog alertDialogAndroid = builder.create();
        alertDialogAndroid.show();
    }

    private boolean pawnPromo() {
        if(head.getGame().pawnPromo()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_pawn_promo)
                    .setItems(R.array.pawn_promo_options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    head.getGame().pawnPromo(Queen.class);
                                    break;
                                case 1:
                                    head.getGame().pawnPromo(Bishop.class);
                                    break;
                                case 2:
                                    head.getGame().pawnPromo(Rook.class);
                                    break;
                                case 3:
                                    head.getGame().pawnPromo(Bishop.class);
                                    break;
                            }
                            refreshBoard();
                        }
                    });
            builder.setCancelable(false);
            builder.show();
            return true;
        }

        return false;
    }

    private void refreshBoard() {
        ChessGame game = head.getGame();

        if (game.getCurrentTeam() == 'w')
            currentTeam.setText(R.string.current_team_prompt_white);
        else if (game.getCurrentTeam() == 'b')
            currentTeam.setText(R.string.current_team_prompt_black);

        chessPieceAdapter.notifyDataSetChanged();

        if (game.checkmate()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            if (game.getCurrentTeam() == 'w')
                builder.setTitle(R.string.dialog_black_wins);
            else
                builder.setTitle(R.string.dialog_white_wins);

            builder.setMessage(R.string.dialog_checkmate_secondary);

            builder.setCancelable(false);
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    saveGameOption();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (game.check()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.dialog_check_main);

            if (game.getCurrentTeam() == 'w')
                builder.setMessage(R.string.dialog_check_secondary_white);
            else
                builder.setMessage(R.string.dialog_check_secondary_black);

            builder.setPositiveButton(R.string.ok, null);

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    public void resign(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (head.getGame().getCurrentTeam() == 'w'){
            builder.setTitle(R.string.dialog_black_wins);
            builder.setMessage("White team has resigned");
        }
        else {
            builder.setTitle(R.string.dialog_white_wins);
            builder.setMessage("Black team has resigned");
        }

        builder.setCancelable(false);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveGameOption();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void ai(View view) {
        ChessGame newGame = head.getGame().clone();
        ChessGameNode newNode = new ChessGameNode(newGame, head);

        if (!newGame.randomMove()) {
            head = head.getPrev();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.error);
            builder.setMessage(R.string.dialog_ai_error_secondary);

            builder.setPositiveButton(R.string.ok, null);

            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            head.setNext(newNode);
            head = newNode;
            chessPieceAdapter.setNode(head);

            undoAllowed = true;
            refreshBoard();
            pawnPromo();
        }
    }

    public void draw(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        if (head.getGame().getCurrentTeam() == 'w'){
            builder.setTitle("White team has proposed a draw");
            builder.setMessage("Black team to you wish to accept the draw?\nThe game will result in a tie.");
        }
        else {
            builder.setTitle("Black team has proposed a draw");
            builder.setMessage("White team to you wish to accept the draw?\nThe game will result in a tie.");
        }

        builder.setPositiveButton("Confirm",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveGameOption();
                    }
                });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public String getOldColor(int row, char col){

        String light = "#ffffff";
        String dark = "#ECEFF1";
        boolean isOdd;
        if ( (row % 2) == 0){ isOdd = false; }
        else{ isOdd = true; }

       switch(col){
           case 'a':
               if (isOdd == false){ return dark; } //even
               else { return light; } //odd

           case 'b':
               if (isOdd == false){ return light; } //even
               else { return dark; } //odd

           case 'c':
               if (isOdd == false){ return dark; } //even
               else { return light; } //odd

           case 'd':
               if (isOdd == false){ return light; } //even
               else { return dark; } //odd

           case 'e':
               if (isOdd == false){ return dark; } //even
               else { return light; } //odd

           case 'f':
               if (isOdd == false){ return light; } //even
               else { return dark; } //odd

           case 'g':
               if (isOdd == false){ return dark; } //even
               else { return light; } //odd

           case 'h':
               if (isOdd == false){ return light; } //even
               else { return dark; } //odd
       }

        return dark;
    }


}
