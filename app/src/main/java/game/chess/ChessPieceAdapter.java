package game.chess;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ImageViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import model.chess.Bishop;
import model.chess.ChessPiece;
import model.chess.King;
import model.chess.Knight;
import model.chess.Pawn;
import model.chess.Queen;
import model.chess.Rook;

public class ChessPieceAdapter extends BaseAdapter {
    private Context context;
    private ChessGameNode head;

    ChessPieceAdapter(Context c, ChessGameNode head) {
        this.context = c;
        this.head = head;
    }

    public ChessGameNode getCurrentNode() {
        return head;
    }

    public void setNode(ChessGameNode node) {
        head = node;
    }

    public int getCount() {
        return 64;
    }

    public ChessPiece getItem(int position) {
        return head.getGame().getPiece(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ChessPiece piece;
        ImageView imageView;

        imageView = new ImageView(context);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(85, 85));
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setPadding(8, 8, 8, 8);

        piece = getItem(position);

        boolean alternateFill = false;
        if (position > 7) {
            if (position > 15) {
                if (position > 23) {
                    if (position > 31) {
                        if (position > 39) {
                            if (position > 47) {
                                if (position <= 55) {
                                    alternateFill = true;
                                }
                            }
                        } else {
                            alternateFill = true;
                        }
                    }
                } else {
                    alternateFill = true;
                }
            }
        } else {
            alternateFill = true;
        }

        if (alternateFill) {
            if ((position % 2) == 0)
                imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.boardSpaceColor));
        } else {
            if ((position % 2) == 1)
                imageView.setBackgroundColor(ContextCompat.getColor(context, R.color.boardSpaceColor));
        }

        if (piece == null)
            return imageView;

        if (piece instanceof Pawn)
            imageView.setImageResource(R.drawable.chess_pawn);

        if (piece instanceof Rook)
            imageView.setImageResource(R.drawable.chess_rook);

        if (piece instanceof Knight)
            imageView.setImageResource(R.drawable.chess_knight);

        if (piece instanceof Bishop)
            imageView.setImageResource(R.drawable.chess_bishop);

        if (piece instanceof Queen)
            imageView.setImageResource(R.drawable.chess_queen);

        if (piece instanceof King)
            imageView.setImageResource(R.drawable.chess_king);

        if (piece.getTeam() == 'w')
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.whiteChessPieceColor)));
        else
            ImageViewCompat.setImageTintList(imageView, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.blackChessPieceColor)));

        return imageView;
    }
}
