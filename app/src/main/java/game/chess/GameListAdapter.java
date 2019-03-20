package game.chess;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class GameListAdapter extends RecyclerView.Adapter<GameListAdapter.ViewHolder> {
    private List<SavedChessGame> games;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name, moves, time;

        ViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            moves = (TextView) view.findViewById(R.id.moves);
            time = (TextView) view.findViewById(R.id.time);
        }
    }

    GameListAdapter(List<SavedChessGame> games) {
        this.games = games;
    }

    public List<SavedChessGame> getGames() {
        return games;
    }

    public SavedChessGame getGame(int position) {
        return games.get(position);
    }

    public void addGame(int position, SavedChessGame item) {
        games.add(position, item);
        notifyItemInserted(position);
    }

    public void removeGame(int position) {
        games.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public @NonNull GameListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.saved_game_list_row, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavedChessGame savedGame = games.get(position);

        if(savedGame.getName() != null){
            if(!savedGame.getName().isEmpty())
                holder.name.setText(savedGame.getName());
        }

        String numMoves = savedGame.getNumOfMoves() + " moves";
        holder.moves.setText(numMoves);

        String dateString = SimpleDateFormat.getDateInstance().format(savedGame.getTime().getTime());
        String timeString = SimpleDateFormat.getTimeInstance().format(savedGame.getTime().getTime());
        String fullDateTime = dateString + " at " + timeString;
        holder.time.setText(fullDateTime);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }
}
