package com.xwdz.version.entry;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/30
 */
public enum QuietStyle {

    Downloader_Notification(1),

    Downloader_Dialog(0);

    private int style;

    QuietStyle(int style) {
        this.style = style;
    }

    public int getStyle() {
        return style;
    }
}
