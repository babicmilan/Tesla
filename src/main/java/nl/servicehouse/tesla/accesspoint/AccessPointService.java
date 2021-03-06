package nl.servicehouse.tesla.accesspoint;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AccessPointService {

    Page<AccessPoint> getAllAccessPoints(Pageable pageable);
    AccessPoint getAccessPointByEAN(String ean);
    AccessPoint createAccessPoint(AccessPoint accessPoint);
    AccessPoint updateAccessPoint(AccessPoint accessPoint);
    void deleteAccessPoint(Long id);
    Optional<AccessPoint> getAccessPointById(Long id);

}
