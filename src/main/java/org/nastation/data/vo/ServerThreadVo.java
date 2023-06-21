package org.nastation.data.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerThreadVo {

    private String serverName;

    private boolean isRun;

    private boolean isEnd;

    private boolean isError;

    private boolean isReady;

    private long sleepMillis;

}
