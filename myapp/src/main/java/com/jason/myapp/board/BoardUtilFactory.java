package com.jason.myapp.board;

import com.meiguan.ipsplayer.base.BuildConfig;

/**
 * Created by qiuzi on 16/5/3.
 */
public class BoardUtilFactory {

    private String board;

    private static BoardUtilFactory instance = new BoardUtilFactory();

    public static BoardUtilFactory getInstance() {
        return instance;
    }

    private BoardUtilFactory() {
        board = BuildConfig.BOARD;
    }

    public static void main(String[] args) {
        BoardUtilFactory.getInstance().getBoardUtil();
    }

    public IBoardUtil getBoardUtil() throws RuntimeException {
        if (board == null) {
            throw new RuntimeException("board type is null!");
        }
        if (board.equals("AIDIWEI-3188")) {
            return new Aidiwei3188Util();
        } else if (board.equals("GUOWEI-A20")) {
            return new GuoweiA20Util();
        } else if (board.equals("SHIXIN-A20")) {
            return new ShixinA20Util();
        } else if (board.equals("SANQUAN-3288")) {
            return new Sanquan3288Util();
        } else if (board.equals("AIDIWEI-3288")) {
            return new Aidiwei3288Util();
        } else if (board.equals("JULI-S500")) {
            return new JuLiS500Util();
        } else if (board.equals("LAND-3188")) {
            return new Land3188Util();
        } else if (board.equals("AIDIWEI-Z05")) {
            return new AidiweiZ05Util();
        } else if (board.equals("SANQUAN-3188")) {
            return new Sanquan3188Util();
        } else if (board.equals("GUOWEI-A83")) {
            return new GuoweiA83Util();
        } else if (board.equals("AIDIWEI-3328")) {
            return new Aidiwei3328Util();
        } else if (board.equals("PHILIPS-V551")) {
            return new Philipv551Util();
        } else if (board.equals("RK-322x")) {
            return new RK322xUtil();
        } else if (board.equals("YS-3399")) {
            return new YS3399Util();
        } else if (board.equals("YS-3128")) {
            return new YS3128Util();
        } else if (board.equals("FANGJI-5159")) {
            return new FangJi5159Util();
        } else if (board.equals("FANGJI-5166")) {
            return new FangJi5166Util();
        } else {
            throw new RuntimeException("board type is incorrectly configured!");
        }
    }

    public String getBoard() {
        return board;
    }

}
