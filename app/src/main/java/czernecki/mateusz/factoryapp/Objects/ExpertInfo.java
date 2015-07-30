package czernecki.mateusz.factoryapp.Objects;

import czernecki.mateusz.factoryapp.Abstracts.IExpertInfo;

public class ExpertInfo implements IExpertInfo {

    private String mail;

    public ExpertInfo(String mail) {
        this.mail = mail;
    }

    @Override
    public String getEmail() {
        return this.mail;
    }
}
