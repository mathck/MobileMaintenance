package czernecki.mateusz.factoryapp.Objects;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import czernecki.mateusz.factoryapp.Abstracts.AMachine;
import czernecki.mateusz.factoryapp.Abstracts.IChecklist;
import czernecki.mateusz.factoryapp.Abstracts.IExpertInfo;
import czernecki.mateusz.factoryapp.Enums.MachineState;

public class Machine extends AMachine {

    public Machine(String parseId, String name, String desc, boolean turnedOn, MachineState state, IExpertInfo expert, List<IChecklist> checklists) {
        this.parseId = parseId;
        this.name = name;
        this.description = desc;
        this.turnedOn = turnedOn;
        this.state = state;
        this.expert = expert;
        this.checklists = checklists;
        this.sortValue = getSortValue();
    }

    public Machine(Parcel in) {
        String[] data = new String[5];

        in.readStringArray(data);
        this.name = data[0];
        this.description = data[1];
        this.state = MachineState.values()[Integer.parseInt(data[2])];
        this.turnedOn = Integer.parseInt(data[3]) > 0;
        this.expert = new ExpertInfo(data[4]);
        this.sortValue = getSortValue();
    }

    public int getSortValue() {
        int result = 0;

        result += (this.state.ordinal() * 2);
        result += this.turnedOn ? 1 : 0;

        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.name,
                this.description,
                Integer.toString(this.state.ordinal()),
                Integer.toString(this.turnedOn ? 1 : 0),
                this.expert.getEmail()});
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Machine createFromParcel(Parcel in) {
            return new Machine(in);
        }

        public Machine[] newArray(int size) {
            return new Machine[size];
        }
    };
}
