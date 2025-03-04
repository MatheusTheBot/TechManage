package matheus_henrique.TechManage.Enums;

import lombok.Getter;

@Getter
public enum EUserType {
    ADMIN("ADMIN"),

    EDITOR("EDITOR"),

    VIEWER("VIEWER");

    private String value;

    EUserType(String value) {
        this.value = value;
    }
}