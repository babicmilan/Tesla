package nl.servicehouse.tesla.accesspoint;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import nl.servicehouse.billingengine.metering.domain.AccessPoint;
import nl.servicehouse.billingengine.metering.dto.AccessPointDto;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AccessPointServiceImpl implements AccessPointService {


    private final AccessPointRepository accessPointRepository;

    @Autowired
    public AccessPointServiceImpl(AccessPointRepository accessPointRepository){
        this.accessPointRepository = accessPointRepository;
    }

    @Override
    public Page<AccessPoint> getAllAccessPoints(Pageable pageable) {
        return this.accessPointRepository.findAll(pageable);
    }

    @Override
    public Optional<AccessPoint> getAccessPointById(Long id) {
        return Optional.ofNullable(this.accessPointRepository.findOne(id));
    }

    @Override
    public AccessPoint getAccessPointByEAN(String ean) {
        return this.accessPointRepository.findByEan(ean);
    }

    @Override
    public AccessPoint createAccessPoint(AccessPoint accessPoint) {
        return this.accessPointRepository.save(accessPoint);
    }

    @Override
    public AccessPoint updateAccessPoint(AccessPoint accessPoint) {
        if(accessPoint.getId() == null) {
            return null;
        }
        //AccessPoint accessPoint = mapDtoToEntity(accessPointDto);
        AccessPoint accessPointDB = this.accessPointRepository.findOne(accessPoint.getId());
        BeanUtils.copyProperties(accessPoint, accessPointDB, "id", "meters");

        return this.accessPointRepository.save(accessPointDB);
    }

    @Override
    public void deleteAccessPoint(Long id) {
        this.accessPointRepository.delete(id);
    }

}
