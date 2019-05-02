package org.ringingmaster.ui.desktop.setuppanel;

import com.google.common.base.Strings;
import org.ringingmaster.engine.composition.MutableComposition;
import org.ringingmaster.engine.notation.Notation;
import org.ringingmaster.engine.notation.NotationBuilder;

public class SetStartNotationHandler {


    public void setStartNotation(MutableComposition composition, String startNotation) {

        if (Strings.isNullOrEmpty(startNotation)) {
            composition.removeStartNotation();
            return;
        }

        Notation builtNotation = NotationBuilder.getInstance()
                .setNumberOfWorkingBells(composition.get().getNumberOfBells())
                .setUnfoldedNotationShorthand(startNotation)
                .build();

        if (builtNotation != null && builtNotation.size() > 0) {
            composition.setStartNotation(builtNotation);
        } else {
            composition.removeStartNotation();
        }
    }
}
