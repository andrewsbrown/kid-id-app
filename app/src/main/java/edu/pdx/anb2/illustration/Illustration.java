package edu.pdx.anb2.illustration;

import edu.pdx.anb2.R;

public class Illustration {

    public final static Illustration[] ALL = new Illustration[]{
            new Illustration(R.drawable.bear, "bear"),
            new Illustration(R.drawable.dog, "dog"),
            new Illustration(R.drawable.elephant, "elephant"),
            new Illustration(R.drawable.giraffe, "giraffe"),
            new Illustration(R.drawable.horse, "horse"),
            new Illustration(R.drawable.kangaroo, "kangaroo"),
            new Illustration(R.drawable.lion, "lion"),
            new Illustration(R.drawable.peacock, "peacock"),
            new Illustration(R.drawable.rhino, "rhino"),
            new Illustration(R.drawable.tiger, "tiger")
    };

    public final int image;
    public final String name;

    public static int find(int image) {
        for (int i = 0; i < ALL.length; i++) {
            if (ALL[i].image == image) return i;
        }
        return -1;
    }

    public Illustration(int image, String name) {
        this.image = image;
        this.name = name;
    }
}
