package com.wss.ghostwriter.bb.widgets;

import org.metova.bb.widgets.field.label.NullLabelField;
import org.metova.bb.widgets.field.richcontent.RichTextField;
import org.metova.bb.widgets.manager.AbstractHorizontalFieldManager;
import org.metova.mobile.util.text.Text;

public class ErrorManager extends AbstractHorizontalFieldManager {

    private RichTextField labelField;

    public ErrorManager() {

        this( 0 );
    }

    public ErrorManager(long style) {

        super( style | USE_ALL_WIDTH | NO_HORIZONTAL_SCROLL );
    }

    protected void initializeFields() {

    // do nothing
    }

    protected void addFields() {

        RichTextField labelField = getLabelField();
        if (labelField != null) {

            add( labelField );
        }
        else {

            add( new NullLabelField( 1, 0 ) );
        }
    }

    public void setText( String text ) {

        RichTextField labelField = null;

        if (Text.isNull( text ) == false) {

            labelField = new RichTextField( text, NON_FOCUSABLE );
            labelField.getStyleManager().setStyleClass( "LabelField-error" );
        }

        setLabelField( labelField );

        reload();
    }

    private RichTextField getLabelField() {

        return labelField;
    }

    private void setLabelField( RichTextField labelField ) {

        this.labelField = labelField;
    }
}
