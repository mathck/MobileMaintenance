package czernecki.mateusz.factoryapp.Abstracts;

import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

import czernecki.mateusz.factoryapp.Enums.MachineState;
import czernecki.mateusz.factoryapp.Objects.Machine;

public abstract class AMachine implements Comparable<Machine>, Parcelable {
    public String id;
    public String url;
    public String parseId;
    public String name;
    public String description;
    public MachineState state;
    public boolean turnedOn;
    public IExpertInfo expert;
    public List<IChecklist> checklists;
    public int sortValue;

    @Override
    public int compareTo(@NonNull Machine compareMachine) {
        return compareMachine.getSortValue() - this.sortValue;
    }
}
