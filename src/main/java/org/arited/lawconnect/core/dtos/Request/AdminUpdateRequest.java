package org.arited.lawconnect.core.dtos.Request;

import lombok.Getter;
import lombok.Setter;
import org.arited.lawconnect.core.enums.AccesEnum;

@Getter
@Setter
public class AdminUpdateRequest {

    private String fullName;

    private AccesEnum niveauAcces;
}
