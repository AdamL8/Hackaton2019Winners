package ca.qc.hackathon2019.importtensorflowastf.ui.main;

import java.util.List;

import ca.qc.hackathon2019.importtensorflowastf.data.model.Ribot;
import ca.qc.hackathon2019.importtensorflowastf.ui.base.MvpView;

public interface MainMvpView extends MvpView {

    void showRibots(List<Ribot> ribots);

    void showRibotsEmpty();

    void showError();

}
