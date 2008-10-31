package com.wss.ghostwriter.bb.widgets.choice;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.XYRect;

import org.metova.bb.widgets.field.choice.ArrayChoiceField;
import org.metova.bb.widgets.field.choice.ChoiceField;
import org.metova.bb.widgets.field.label.LabelField;
import org.metova.bb.widgets.field.label.headers.H3LabelField;
import org.metova.bb.widgets.manager.AbstractHorizontalFieldManager;

public class ChoiceSection extends AbstractHorizontalFieldManager {

    private LabelField labelField;
    private ArrayChoiceField arrayChoiceField;

    private String label;
    private String[] choices;
    private String initialValue;

    public ChoiceSection(String label, String[] choices, String initialValue) {

        super( USE_ALL_WIDTH | NO_HORIZONTAL_SCROLL );

        setLabel( label );
        setChoices( choices );
        setInitialValue( initialValue );
    }

    protected void initializeFields() {

        H3LabelField h3LabelField = new H3LabelField( getLabel() );
        h3LabelField.getStyleManager().setStyleClass( "h3-margin-top" );
        setLabelField( h3LabelField );

        setArrayChoiceField( new ArrayChoiceField( null, getChoices(), getInitialValue(), ChoiceField.FIELD_LEFT ) );
    }

    protected void addFields() {

        add( getLabelField() );
        add( getArrayChoiceField() );
    }

    protected void onSublayout( int maxWidth, int maxHeight ) {

        super.onSublayout( maxWidth, maxHeight );

        int height = getHeight();

        ArrayChoiceField arrayChoiceField = getArrayChoiceField();
        if ( arrayChoiceField != null && equals( arrayChoiceField.getManager() ) ) {

            XYRect extent = new XYRect();
            arrayChoiceField.getExtent( extent );

            Font arrayChoiceFieldFont = arrayChoiceField.getFont();
            int arrayChoiceFieldBaseline = arrayChoiceField.getHeight() - ( arrayChoiceFieldFont.getHeight() - arrayChoiceFieldFont.getBaseline() );

            int y = (int) ( ( height - arrayChoiceFieldBaseline ) * 0.50 );

            setPositionChild( arrayChoiceField, extent.x, y );
        }
    }

    public ArrayChoiceField getArrayChoiceField() {

        return arrayChoiceField;
    }

    private void setArrayChoiceField( ArrayChoiceField arrayChoiceField ) {

        this.arrayChoiceField = arrayChoiceField;
    }

    private String getLabel() {

        return label;
    }

    private void setLabel( String label ) {

        this.label = label;
    }

    protected LabelField getLabelField() {

        return labelField;
    }

    private void setLabelField( LabelField labelField ) {

        this.labelField = labelField;
    }

    private String[] getChoices() {

        return choices;
    }

    private void setChoices( String[] choices ) {

        this.choices = choices;
    }

    private String getInitialValue() {

        return initialValue;
    }

    private void setInitialValue( String initialValue ) {

        this.initialValue = initialValue;
    }
}
