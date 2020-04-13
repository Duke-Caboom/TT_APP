package Models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.usersjava.BR;

public class Item extends BaseObservable {
    private int selectedItemPosition;

    /*La anotación enlazable debe aplicarse a cualquier metodo de acceso getter de una ObservableClase.
     * Bindable generará un campo en la clase BR para identificar el campo que ha cambiado*/
    @Bindable
    public int getSelectedItemPosition() {
        return selectedItemPosition;
    }

    public void setSelectedItemPosition(int selectedItemPosition) {
        this.selectedItemPosition = selectedItemPosition;
        notifyPropertyChanged(BR.selectedItemPosition);
    }
}
