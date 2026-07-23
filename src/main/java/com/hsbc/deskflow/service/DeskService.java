package com.hsbc.deskflow.service;

import com.hsbc.deskflow.domain.Desk;
import com.hsbc.deskflow.dto.DeskResponse;
import com.hsbc.deskflow.repository.DeskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DeskService {

    private final DeskRepository deskRepository;

    public DeskService(DeskRepository deskRepository) {
        this.deskRepository = deskRepository;
    }

    @Transactional(readOnly = true)
    public List<DeskResponse> listDesks(Integer floor, Boolean hasMonitor) {
        List<Desk> desks;
        if (floor != null && hasMonitor != null) {
            desks = deskRepository.findByFloorAndHasMonitor(floor, hasMonitor);
        } else if (floor != null) {
            desks = deskRepository.findByFloor(floor);
        } else if (hasMonitor != null) {
            desks = deskRepository.findByHasMonitor(hasMonitor);
        } else {
            desks = deskRepository.findAll();
        }
        return desks.stream().map(this::toResponse).toList();
    }

    private DeskResponse toResponse(Desk desk) {
        return new DeskResponse(
                desk.getId(),
                desk.getCode(),
                desk.getFloor(),
                desk.isHasMonitor(),
                desk.isActive()
        );
    }
}
