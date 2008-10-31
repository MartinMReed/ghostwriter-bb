package com.wss.ghostwriter.bb.widgets.choice;

import net.rim.device.api.ui.XYRect;

import org.metova.bb.widgets.field.checkbox.CheckboxField;
import org.metova.bb.widgets.field.label.LabelField;
import org.metova.bb.widgets.field.label.headers.H3LabelField;
import org.metova.bb.widgets.manager.AbstractHorizontalFieldManager;

public class CheckboxSection extends AbstractHorizontalFieldManager {

    private LabelField labelField;
    private CheckboxField checkboxField;

    private String label;
    private boolean checked;

    public CheckboxSection(String label, boolean checked) {

        super( USE_ALL_WIDTH | NO_HORIZONTAL_SCROLL );

        setLabel( label );
        setChecked( checked );
    }

    protected void initializeFields() {

        H3LabelField h3LabelField = new H3LabelField( getLabel() );
        h3LabelField.getStyleManager().setStyleClass( "h3-margin-top" );
        setLabelField( h3LabelField );

        CheckboxField checkboxField = new CheckboxField();
        checkboxField.setChecked( isChecked() );
        setCheckboxField( checkboxField );
    }

    protected void addFields() {

        add( getLabelField() );
        add( getCheckboxField() );
    }

    protected void onSublayout( int maxWidth, int maxHeight ) {

        super.onSublayout( maxWidth, maxHeight );

        int height = getHeight();

        CheckboxField checkboxField = new CheckboxField();
        if ( checkboxField != null && equals( checkboxField.getManager() ) ) {

            XYRect extent = new XYRect();
            checkboxField.getExtent( extent );

            int y = (int) ( ( height - checkboxField.getHeight() ) * 0.50 );

            setPositionChild( checkboxField, extent.x, y );
        }
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

    public CheckboxField getCheckboxField() {

        return checkboxField;
    }

    private void setCheckboxField( CheckboxField checkboxField ) {

        this.checkboxField = checkboxField;
    }

    private boolean isChecked() {

        return checked;
    }

    private void setChecked( boolean checked ) {

        this.checked = checked;
    }
}
