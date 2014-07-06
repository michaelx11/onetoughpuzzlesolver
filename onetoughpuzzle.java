import java.util.*;
import java.io.*;

/*
 * Spades:    1
 * Hearts:    2
 * Clubs:     3
 * Diamonds:  4
 *
 * Out = Positive, In = Negative
 *
 *
 * Standard configuration: 
 *
 *      out
 *  out     in
 *      in
 *
 * Number your pieces and input them.
 */

enum Joint {
  SPADE_OUT,
  SPADE_IN,
  HEART_OUT,
  HEART_IN,
  CLUB_OUT,
  CLUB_IN,
  DIAMOND_OUT,
  DIAMOND_IN, 
  ALL 
}

class JointComp {

  public static boolean isOk(Joint a, Joint b) {
    if (a == Joint.ALL || b == Joint.ALL)
      return true;
    if(a == Joint.SPADE_OUT && b == Joint.SPADE_IN)
      return true;
    if(b == Joint.SPADE_OUT && a == Joint.SPADE_IN)
      return true;
    if(a == Joint.HEART_OUT && b == Joint.HEART_IN)
      return true;
    if(b == Joint.HEART_OUT && a == Joint.HEART_IN)
      return true;
    if(a == Joint.CLUB_OUT && b == Joint.CLUB_IN)
      return true;
    if(b == Joint.CLUB_OUT && a == Joint.CLUB_IN)
      return true;
    if(a == Joint.DIAMOND_OUT && b == Joint.DIAMOND_IN)
      return true;
    if(b == Joint.DIAMOND_OUT && a == Joint.DIAMOND_IN)
      return true;

    return false;
  }
}

class Piece {
  public int id;

  public Joint[] joints = new Joint[4];

  public int rotation;

  public static Piece NULL_PIECE = new Piece(-1, Joint.ALL, Joint.ALL, Joint.ALL, Joint.ALL, 0); 

  public Piece(int id, Joint top, Joint right, Joint down, Joint left, int rotation) {
    this.id = id;
    joints[0] = top;
    joints[1] = right;
    joints[2] = down;
    joints[3] = left;
    this.rotation = rotation;
  }

  private Joint getJointWithRotation(int initial) {
    int mod = (initial + rotation) % 4;
    return joints[mod];
  }

  public Joint getTop() {
    return getJointWithRotation(0);
  }

  public Joint getRight() {
    return getJointWithRotation(1);
  }

  public Joint getDown() {
    return getJointWithRotation(2);
  }

  public Joint getLeft() {
    return getJointWithRotation(3);
  }

  public void rotate() {
    this.rotation++;
  }

  public void setRotation(int rot) {
    this.rotation = rot;
  }

  public Piece clone() {
    return new Piece(this.id, joints[0], joints[1], joints[2], joints[3], this.rotation);
  }

  public String toString() {
    return String.format("[%d (r:%d)]", this.id, this.rotation);
  }
}


public class onetoughpuzzle {

  public static Piece[] pieces = new Piece[]{new Piece(1, Joint.CLUB_OUT, Joint.HEART_IN, Joint.SPADE_IN, Joint.HEART_OUT, 0),
                                             new Piece(2, Joint.HEART_OUT, Joint.HEART_IN, Joint.DIAMOND_IN, Joint.DIAMOND_OUT, 0),
                                             new Piece(3, Joint.SPADE_OUT, Joint.CLUB_IN, Joint.HEART_IN, Joint.SPADE_OUT, 0),
                                             new Piece(4, Joint.DIAMOND_OUT, Joint.DIAMOND_IN, Joint.CLUB_IN, Joint.CLUB_OUT, 0),
                                             new Piece(5, Joint.SPADE_OUT, Joint.HEART_IN, Joint.SPADE_IN, Joint.DIAMOND_OUT, 0),
                                             new Piece(6, Joint.SPADE_OUT, Joint.DIAMOND_IN, Joint.HEART_IN, Joint.DIAMOND_OUT, 0),
                                             new Piece(7, Joint.CLUB_OUT, Joint.CLUB_IN, Joint.DIAMOND_IN, Joint.HEART_OUT, 0),
                                             new Piece(8, Joint.HEART_OUT, Joint.CLUB_IN, Joint.SPADE_IN, Joint.SPADE_OUT, 0),
                                             new Piece(9, Joint.HEART_OUT, Joint.CLUB_IN, Joint.CLUB_IN, Joint.DIAMOND_OUT, 0)};

  public static boolean[] used = new boolean[10];

  public static ArrayList<String> solutions = new ArrayList<String>();

  // sentinel border
  public static Piece[][] board = new Piece[5][5];

  static {
    // initialize sentinel pieces
    for (int i = 0; i < 5; i++) {
      for (int u = 0; u < 5; u++) {
        board[i][u] = Piece.NULL_PIECE;
      }
    }
  }

  public static boolean isSquareValid(int r, int c) {
    Piece curr = board[r][c];

    Piece top = board[r - 1][c];
    if (!JointComp.isOk(top.getDown(), curr.getTop()))
      return false;

    Piece left = board[r][c - 1];
    if (!JointComp.isOk(left.getRight(), curr.getLeft()))
      return false;

    Piece down = board[r + 1][c];
    if (!JointComp.isOk(down.getTop(), curr.getDown()))
      return false;

    Piece right = board[r][c + 1];
    if (!JointComp.isOk(right.getLeft(), curr.getRight()))
      return false;

    return true;
  }

  public static String printBoard() {
    String s = "";
    for (int i = 1; i < 4; i++) {
      for (int u = 1; u < 4; u++) {
        s += board[i][u].toString();
      }
      s += "\n";
    }
    return s;
  }

  public static boolean isValid() {
    for (int i = 1; i < 4; i++) {
      for (int u = 1; u < 4; u++) {
        if (!isSquareValid(i, u))
          return false;
      }
    }
    return true;
  }

  public static void recur(int r, int c) {
    if (!isValid()) {
      return;
    }
    if (r == 4) {
      solutions.add(printBoard());
      return;
    }
    int newr = r;
    int newc = c + 1;
    if (newc == 4) {
      newr++;
      newc = 1;
    }

    for (int pindex = 0; pindex < pieces.length; pindex++) {
      if (!used[pindex]) {
        Piece temp = pieces[pindex].clone();
        for (int rot = 0; rot < 4; rot++) {
          temp.setRotation(rot);
          board[r][c] = temp;
          used[pindex] = true;
          recur(newr, newc);
          used[pindex] = false;
          board[r][c] = Piece.NULL_PIECE; 
        }
      }
    }
  }

  public static void main(String ... args) throws IOException {
    recur(1,1);
    for (String solution : solutions) {
      System.out.println(solution);
    }
  }
}
