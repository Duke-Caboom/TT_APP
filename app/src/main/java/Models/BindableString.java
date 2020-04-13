package Models;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;
import androidx.databinding.library.baseAdapters.BR;

import java.util.Objects;

public class BindableString extends BaseObservable {

    private String value;

    /*BseObservable que puede amoliar. La clase ed datos es responsable de notificar cuando cambias las propiedades. Esto se hace asignando una anotacion
    @Budable al captador y notificandolo en el definidor. Este oyente se invoca en cada actualizacion y actualiza las vistas correspondientes */
    @Bindable
    public String getValue() {
        return value != null ? value : "";
    }

    public void setValue(String value) {
        if (!Objects.equals(this.value, value)) {
            this.value = value;
            notifyPropertyChanged(BR.value);
        }
    }
}
