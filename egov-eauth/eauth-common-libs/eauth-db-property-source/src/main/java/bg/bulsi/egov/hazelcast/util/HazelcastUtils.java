package bg.bulsi.egov.hazelcast.util;

import bg.bulsi.egov.hazelcast.enums.OTPMethods;
import bg.bulsi.egov.hazelcast.service.HazelcastService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.IMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class HazelcastUtils {

    @Autowired
    HazelcastService hazelcastService;

    /**
     * Get only enabled OTPMethods from hazelecast map
     *
     */
    public List<OTPMethods> getOTPMethodsEnabled() {

        IMap map = hazelcastService.all();

        Iterator<Map.Entry<String, String>> itr = map.entrySet().iterator();
        List<OTPMethods> listEnabledOtp = new ArrayList<>();
        List<String> namesOfEnumsOTP = new ArrayList<>();
        for (OTPMethods e : OTPMethods.values()){
            namesOfEnumsOTP.add( e.key());
        }

        while(itr.hasNext()) {

            Map.Entry<String, String> entry = itr.next();
            if(entry.getKey().startsWith(OTPMethods.getPREFIX())) {
                if(entry.getValue().equals("true")) {
                    if(namesOfEnumsOTP.contains(entry.getKey())){
                       OTPMethods m = OTPMethods.enumKey(entry.getKey());
                        listEnabledOtp.add(m);
                    }
                }
            }
        }
        return listEnabledOtp;
    }
}
