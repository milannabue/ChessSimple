//package com.example.chess.view;
//
//import static java.security.AccessController.getContext;
//
//import android.content.Context;
//import android.graphics.Color;
//import android.util.AttributeSet;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.Nullable;
//
//public class ChessBoardView extends GridLayout {
//
//    public ChessBoardView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        initializeBoard();
//    }
//
//    private void initializeBoard() {
//        for (int row = 0; row < 8; row++) {
//            for (int col = 0; col < 8; col++) {
//                TextView square = new TextView(getContext());
//                square.setLayoutParams(new ViewGroup.LayoutParams(
//                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                square.setBackgroundColor((row + col) % 2 == 0 ? Color.LTGRAY : Color.DKGRAY);
//                square.setTag(row + "," + col);
//                addView(square);
//            }
//        }
//    }
//}
