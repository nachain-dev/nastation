
package org.nastation.common.util;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.nachain.core.chain.structure.instance.CoreInstanceEnum;
import org.nachain.core.chain.structure.instance.Instance;
import org.nachain.core.chain.structure.instance.InstanceType;
import org.nachain.core.token.Token;
import org.nachain.core.token.TokenService;
import org.nachain.core.util.JsonUtils;
import org.nastation.data.service.WalletDataService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InstanceUtil {

    public static List<Instance> values() {

        List<Instance> instanceList = null;

        if (WalletDataService.MERGE_API_PACK_VO != null) {
            instanceList = WalletDataService.MERGE_API_PACK_VO.getInstanceList();
        }

        if (instanceList == null) {
            instanceList = Lists.newArrayList();
            CoreInstanceEnum[] values = CoreInstanceEnum.values();
            for (CoreInstanceEnum each : values) {
                Instance instance = toInstance(each);
                instanceList.add(instance);
            }
        }

        return Lists.newArrayList(instanceList);
    }

    public static Instance getNacInstance() {
        return toInstance(CoreInstanceEnum.NAC);
    }

    public static Instance toInstance(CoreInstanceEnum inst) {
        Instance one = new Instance();
        one.setAppName(inst.name);
        one.setId(inst.id);
        one.setSymbol(inst.symbol);
        return one;
    }

    public static final String CURRENT_INSTANCE_ID = "Current_Instance_ID";

    public static Integer getCurrentInstanceId(HttpServletRequest request) {
        Object obj = request.getSession().getAttribute(CURRENT_INSTANCE_ID);

        if (obj == null) {
            return 1;
        }

        return Integer.valueOf(obj.toString());
    }

    public static void setCurrentInstanceId(HttpServletRequest request, int id) {

        Optional<Instance> instOpt = values().stream()
                .filter(one -> (one.getId() == id))
                .findFirst();

        if (instOpt.isPresent()) {
            request.getSession().setAttribute(CURRENT_INSTANCE_ID, id);
        }

    }

    public static String getInstanceName(HttpServletRequest request) {
        Integer currentInstanceId = getCurrentInstanceId(request);

        Instance inst = values().stream()
                .filter(one -> (one.getId() == currentInstanceId))
                .findFirst().orElse(getNacInstance());

        return inst.getAppName();
    }

    public static String getInstanceName(long instanceId) {

        Instance inst = values().stream()
                .filter(one -> (one.getId() == instanceId))
                .findFirst().orElse(getNacInstance());

        return inst.getAppName();
    }

    public static String getInstanceSymbol(long instanceId) {

        Instance inst = values().stream()
                .filter(one -> (one.getId() == instanceId))
                .findFirst().orElse(getNacInstance());

        return inst.getSymbol();
    }

    public static Instance getInstance(long instanceId) {

        Instance inst = values().stream()
                .filter(one -> (one.getId() == instanceId))
                .findFirst().orElse(getNacInstance());

        return inst;
    }

    public static Instance getInstanceBySymbol(String symbol) {

        Instance inst = values().stream()
                .filter(one -> (one.getSymbol().equalsIgnoreCase(symbol)))
                .findFirst().orElse(getNacInstance());

        return inst;
    }

    public static Instance getInstance(HttpServletRequest request) {
        Integer currentInstanceId = getCurrentInstanceId(request);

        Instance inst = values().stream()
                .filter(one -> (one.getId() == currentInstanceId))
                .findFirst().orElse(getNacInstance());

        return inst;
    }

    public static Instance convert(CoreInstanceEnum instanceEnum) {

        long id = instanceEnum.id;
        String info = instanceEnum.info;
        String name = instanceEnum.name;
        String symbol = instanceEnum.symbol;

        Instance one = new Instance();
        one.setAppName(name);
        one.setId(id);
        one.setInfo(info);
        one.setSymbol(symbol);

        return one;
    }

    public static List<Instance> getEnableInstanceList() {
        Stream<Instance> instStream = values().stream()
                .filter(one -> (one.getId() >= 0));

        return instStream.collect(Collectors.toList());
    }

    public static List<Instance> getNftInstanceList() {
        return InstanceUtil.getEnableInstanceList().stream().filter(e -> isNftToken(e)).collect(Collectors.toList());
    }

    public static boolean isNftToken(Instance instance) {

        if (instance == null) {
            return false;
        }

        if (instance.getInstanceType() == InstanceType.Token) {

            String data = instance.getData();

            if (StringUtils.isBlank(data)) {
                return false;
            }

            Token token = JsonUtils.jsonToPojo(data, Token.class);

            if (token == null) {
                return false;
            }

            return TokenService.isNft(token);
        }

        return false;
    }

    public static void main(String[] args) {
        Optional<Instance> coreInstanceEnum = values().stream()
                .filter(one -> (one.getId() == 50))
                .findFirst();

    }

}