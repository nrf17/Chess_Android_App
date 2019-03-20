package game.chess;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import model.chess.ChessGame;

public class PlayBack extends AppCompatActivity {
    private ChessGameNode head;
    private TextView currentTeam;
    private GridView grid;
    private ChessPieceAdapter chessPieceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_back);

        Bundle bundle = getIntent().getExtras();
        SavedChessGame savedGame = (SavedChessGame) bundle.get("GAME");
        head = savedGame.getFirstNode();

        currentTeam = (TextView) findViewById(R.id.currentTeam);

        if(head.getGame().getCurrentTeam() == 'w')
            currentTeam.setText(R.string.current_team_prompt_white);
        else
            currentTeam.setText(R.string.current_team_prompt_black);

        populateBoard();
    }

    private void populateBoard() {
        grid = (GridView) findViewById(R.id.board);

        chessPieceAdapter = new ChessPieceAdapter(this, head);
        grid.setAdapter(chessPieceAdapter);
    }

    private void refreshBoard() {
        ChessGame game = head.getGame();
        chessPieceAdapter.setNode(head);

        if (game.getCurrentTeam() == 'w')
            currentTeam.setText(R.string.current_team_prompt_white);
        else if (game.getCurrentTeam() == 'b')
            currentTeam.setText(R.string.current_team_prompt_black);

        chessPieceAdapter.notifyDataSetChanged();
    }

    public void forward(View view) {
        if(head.getNext() != null) {
            head = head.getNext();
            refreshBoard();
        }
    }

    public void back(View view) {
        if(head.getPrev() != null) {
            head = head.getPrev();
            refreshBoard();
        }
    }

}
