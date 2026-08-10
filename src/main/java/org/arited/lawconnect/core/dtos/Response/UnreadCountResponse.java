package org.arited.lawconnect.core.dtos.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UnreadCountResponse {
    private long count;
}
