package com.wss.ghostwriter.bb.ui;

import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;

import org.metova.bb.widgets.field.image.BitmapField;
import org.metova.bb.widgets.screen.AbstractManagedScreen;

public class ScreenViewer extends AbstractManagedScreen {

    private BitmapField bitmapField;

    private int[] rgb;

    public ScreenViewer(int[] rgb) {

        super( 0 );

        setRGB( rgb );
    }

    protected void onInitialize() {

        int width = Display.getWidth();
        int height = Display.getHeight();

        Bitmap bitmap = new Bitmap( width, height );
        bitmap.setARGB( getRGB(), 0, width, 0, 0, width, height );

        setBitmapField( new BitmapField( bitmap ) );
    }

    protected void onLoading() {

        add( getBitmapField() );
    }

    private BitmapField getBitmapField() {

        return bitmapField;
    }

    private void setBitmapField( BitmapField bitmapField ) {

        this.bitmapField = bitmapField;
    }

    private int[] getRGB() {

        return rgb;
    }

    private void setRGB( int[] rgb ) {

        this.rgb = rgb;
    }
}
