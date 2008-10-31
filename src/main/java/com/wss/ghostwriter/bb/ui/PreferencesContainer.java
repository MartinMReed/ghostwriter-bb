package com.wss.ghostwriter.bb.ui;

import net.rim.device.api.ui.Trackball;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Menu;

import org.metova.bb.theme.precision.Container;
import org.metova.bb.theme.precision.InputBoxField;
import org.metova.bb.widgets.Menus;
import org.metova.bb.widgets.field.edit.EditField;
import org.metova.bb.widgets.field.label.WrappedTextLabelField;
import org.metova.mobile.util.math.MathUtils;

import com.wss.ghostwriter.bb.model.Preferences;
import com.wss.ghostwriter.bb.service.ConnectionService;
import com.wss.ghostwriter.bb.service.PreferenceStore;
import com.wss.ghostwriter.bb.widgets.choice.CheckboxSection;
import com.wss.ghostwriter.bb.widgets.choice.ChoiceSection;

public class PreferencesContainer extends Container {

    private InputBoxField serverDomainField;
    private ChoiceSection sensitivityField;
    private CheckboxSection keepAliveField;
    private InputBoxField keepAliveDelayField;

    public PreferencesContainer() {

        setSavePromptEnabled( true );
    }

    protected boolean onSave() {

        Preferences preferences = PreferenceStore.instance().load();
        preferences.setServerDomain( getServerDomainField().getBasicEditField().getText() );
        preferences.setSensitivity( Integer.parseInt( (String) getSensitivityField().getArrayChoiceField().getSelectedChoice() ) );
        preferences.setKeepAlive( getKeepAliveField().getCheckboxField().getChecked() );
        preferences.setKeepAliveDelay( Long.parseLong( getKeepAliveDelayField().getBasicEditField().getText() ) );
        PreferenceStore.instance().commit();

        if ( ConnectionService.isAlive() == false ) {

            ConnectionService.reset();
        }

        return super.onSave();
    }

    protected void onInitializeContent() {

        Preferences preferences = PreferenceStore.instance().load();

        EditField serverDomainField = new EditField( EditField.NO_NEWLINE );
        serverDomainField.setText( preferences.getServerDomain() );
        setServerDomainField( new InputBoxField( "Server Domain:", serverDomainField ) );

        int sensitivityIncrement = Trackball.getSensitivityIncrement();
        int sensitivityCount = (int) MathUtils.doubleDivision( 100, sensitivityIncrement );
        String[] choiceStr = new String[sensitivityCount + 1];

        for (int i = sensitivityCount; i >= 0; i--) {

            choiceStr[sensitivityCount - i] = String.valueOf( i * sensitivityIncrement );
        }

        setSensitivityField( new ChoiceSection( "Sensitivity:", choiceStr, String.valueOf( preferences.getSensitivity() ) ) );

        EditField keepAliveDelayField = new EditField( EditField.NO_NEWLINE | EditField.FILTER_INTEGER );
        keepAliveDelayField.setText( String.valueOf( preferences.getKeepAliveDelay() ) );
        setKeepAliveDelayField( new InputBoxField( "Life Span:", keepAliveDelayField ) );

        setKeepAliveField( new CheckboxSection( "Keep Alive:", preferences.isKeepAlive() ) );
    }

    protected void addContentFields() {

        boolean connectionServiceIsAlive = ConnectionService.isAlive();

        EditField serverDomainField = (EditField) getServerDomainField().getBasicEditField();
        serverDomainField.setEditable( connectionServiceIsAlive == false );
        serverDomainField.getStyleManager().setStyleClass( connectionServiceIsAlive ? "disabled-preference" : "EditField" );

        if ( connectionServiceIsAlive ) {

            WrappedTextLabelField wrappedTextLabelField = new WrappedTextLabelField( "You must disconnect before changing server information" );
            wrappedTextLabelField.getStyleManager().setStyleClass( "LabelField-error" );
            add( wrappedTextLabelField );
        }

        add( getServerDomainField() );
        add( getSensitivityField() );
        add( getKeepAliveDelayField() );
        add( getKeepAliveField() );
    }

    protected void makeMenu( Menu menu, int instance ) {

        if ( ConnectionService.isAlive() ) {

            menu.add( Menus.newMenuItemRunNow( "Disconnect", new Runnable() {

                public void run() {

                    ConnectionService.reset();

                    UiApplication.getUiApplication().invokeLater( new Runnable() {

                        public void run() {

                            reload();
                        };
                    } );
                }
            } ) );
        }

        super.makeMenu( menu, instance );
    }

    private InputBoxField getServerDomainField() {

        return serverDomainField;
    }

    private void setServerDomainField( InputBoxField serverDomainField ) {

        this.serverDomainField = serverDomainField;
    }

    private ChoiceSection getSensitivityField() {

        return sensitivityField;
    }

    private void setSensitivityField( ChoiceSection sensitivityField ) {

        this.sensitivityField = sensitivityField;
    }

    private CheckboxSection getKeepAliveField() {

        return keepAliveField;
    }

    private void setKeepAliveField( CheckboxSection keepAliveField ) {

        this.keepAliveField = keepAliveField;
    }

    private InputBoxField getKeepAliveDelayField() {

        return keepAliveDelayField;
    }

    private void setKeepAliveDelayField( InputBoxField keepAliveDelayField ) {

        this.keepAliveDelayField = keepAliveDelayField;
    }
}
