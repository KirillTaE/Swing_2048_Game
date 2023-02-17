package entity;

import java.awt.*;


public class Tile {
    public int value;

    public Tile(int value) {
        this.value = value;
    }

    public Tile() {
        this.value = 0;
    }

    public boolean isEmpty() {
        return (this.value == 0);
    }

    //возвращающий новый цвет
    public Color getFontColor() {
        if (value < 16) return new Color(0x776e65);
        else return new Color(0xf9f6f2);
    }

    //возвращающий цвет плитки в зависимости от ее веса
    public Color getTileColor() {
        switch (value) {
            case 0:
                return new Color(0xcdc1b4);
            case 2:
                return new Color(0xeee4da);
            case 4:
                return new Color(0xede0c8);
            case 8:
                return new Color(0xf2b179);
            case 16:
                return new Color(0xf59563);
            case 32:
                return new Color(0xf67c5f);
            case 64:
                return new Color(0xf65e3b);
            case 128:
                return new Color(0xedcf72);
            case 256:
                return new Color(0xedcc61);
            case 512:
                return new Color(0xedc850);
            case 1024:
                return new Color(0xedc53f);
            case 2048:
                return new Color(0xedc22e);
            default:
                return new Color(0xff0000);
        }
    }
}
