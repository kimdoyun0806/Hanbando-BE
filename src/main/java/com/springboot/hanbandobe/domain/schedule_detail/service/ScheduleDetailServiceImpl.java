package com.springboot.hanbandobe.domain.schedule_detail.service;

import com.springboot.hanbandobe.domain.schedule.repository.ScheduleRepository;
import com.springboot.hanbandobe.domain.schedule_detail.dto.ScheduleDetailPutTimeDto;
import com.springboot.hanbandobe.domain.schedule_detail.dto.ScheduleDetailResponseDto;
import com.springboot.hanbandobe.domain.schedule_detail.repository.ScheduleDetailRepository;
import com.springboot.hanbandobe.entity.Schedule;
import com.springboot.hanbandobe.entity.Schedule_detail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleDetailServiceImpl implements ScheduleDetailService {
    private final ScheduleDetailRepository scheduleDetailRepository;
    private final ScheduleRepository scheduleRepository;
    @Override
    public List<ScheduleDetailResponseDto> GetScheduleDetails(Long ScheduleNo) {

        if (!scheduleRepository.existsById(ScheduleNo)) {
            throw new RuntimeException("해당 스케줄이 존재하지 않습니다.");
        }

        List<Schedule_detail> scheduleDetails = scheduleDetailRepository.findAllWithCategoryByScheduleNo(ScheduleNo);

        return scheduleDetails.stream()
                .map(sd -> ScheduleDetailResponseDto.builder()
                        .scheduleDetailNo(sd.getScheduleDetailNo())
                        .travelCategoryName(sd.getTravelCategory().getName())
                        .isSelected(sd.getIsSelected())
                        .title(sd.getTitle())
                        .content(sd.getContent())
                        .startedAt(sd.getStartedAt())
                        .endedAt(sd.getEndedAt())
                        .createdAt(sd.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public ScheduleDetailResponseDto PutScheduleDetailSelect(Long ScheduleDetailNo) {
        Schedule_detail scheduleDetail = scheduleDetailRepository.findById(ScheduleDetailNo)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 일정입니다."));

        if (Boolean.TRUE.equals(scheduleDetail.getIsSelected())) throw new RuntimeException("이미 선택된 일정입니다.");


        scheduleDetail.setIsSelected(Boolean.TRUE);
        scheduleDetail.setUpdatedAt(LocalDateTime.now());
        scheduleDetailRepository.save(scheduleDetail);

        return ScheduleDetailResponseDto.builder()
                .scheduleDetailNo(scheduleDetail.getScheduleDetailNo())
                .travelCategoryName(scheduleDetail.getTravelCategory().getName())
                .isSelected(scheduleDetail.getIsSelected())
                .title(scheduleDetail.getTitle())
                .content(scheduleDetail.getContent())
                .startedAt(scheduleDetail.getStartedAt())
                .endedAt(scheduleDetail.getEndedAt())
                .createdAt(scheduleDetail.getCreatedAt())
                .updatedAt(scheduleDetail.getUpdatedAt())
                .build();
    }

    @Override
    public ScheduleDetailResponseDto PutScheduleDetailCancel(Long ScheduleDetailNo) {
        Schedule_detail scheduleDetail = scheduleDetailRepository.findById(ScheduleDetailNo)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 일정입니다."));

        if (Boolean.FALSE.equals(scheduleDetail.getIsSelected())) throw new RuntimeException("선택된 적이 없는 일정입니다.");

        scheduleDetail.setIsSelected(Boolean.FALSE);
        scheduleDetail.setUpdatedAt(LocalDateTime.now());
        scheduleDetailRepository.save(scheduleDetail);

        return ScheduleDetailResponseDto.builder()
                .scheduleDetailNo(scheduleDetail.getScheduleDetailNo())
                .travelCategoryName(scheduleDetail.getTravelCategory().getName())
                .isSelected(scheduleDetail.getIsSelected())
                .title(scheduleDetail.getTitle())
                .content(scheduleDetail.getContent())
                .startedAt(scheduleDetail.getStartedAt())
                .endedAt(scheduleDetail.getEndedAt())
                .createdAt(scheduleDetail.getCreatedAt())
                .updatedAt(scheduleDetail.getUpdatedAt())
                .build();
    }

    @Override
    public ScheduleDetailResponseDto PutScheduleDetail(Long scheduleDetailNo, ScheduleDetailPutTimeDto dto) {
        Schedule_detail scheduleDetail = scheduleDetailRepository.findById(scheduleDetailNo)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 일정입니다."));

        LocalDateTime newStart = dto.getStartedAt();
        LocalDateTime newEnd = dto.getEndedAt();

        Schedule schedule = scheduleDetail.getSchedule();
        if (newStart.isBefore(schedule.getStartedAt()) || newEnd.isAfter(schedule.getEndedAt())) {
            throw new RuntimeException("일정 시간은 전체 스케줄 범위를 벗어날 수 없습니다.");
        }

        List<Schedule_detail> selectedDetails = scheduleDetailRepository.findBySchedule_ScheduleNoAndIsSelectedTrue(schedule.getScheduleNo());

        for (Schedule_detail other : selectedDetails) {
            if (!other.getScheduleDetailNo().equals(scheduleDetailNo)) {
                LocalDateTime otherStart = other.getStartedAt();
                LocalDateTime otherEnd = other.getEndedAt();

                boolean overlaps = !(newEnd.isBefore(otherStart) || newStart.isAfter(otherEnd));
                if (overlaps) {
                    throw new RuntimeException("겹치는 선택된 일정이 이미 존재합니다.");
                }
            }
        }

        scheduleDetail.setStartedAt(newStart);
        scheduleDetail.setEndedAt(newEnd);
        scheduleDetail.setUpdatedAt(LocalDateTime.now());

        scheduleDetailRepository.save(scheduleDetail);

        return ScheduleDetailResponseDto.builder()
                .scheduleDetailNo(scheduleDetail.getScheduleDetailNo())
                .travelCategoryName(scheduleDetail.getTravelCategory().getName())
                .isSelected(scheduleDetail.getIsSelected())
                .title(scheduleDetail.getTitle())
                .content(scheduleDetail.getContent())
                .startedAt(scheduleDetail.getStartedAt())
                .endedAt(scheduleDetail.getEndedAt())
                .createdAt(scheduleDetail.getCreatedAt())
                .updatedAt(scheduleDetail.getUpdatedAt())
                .build();
    }

    @Override
    public ScheduleDetailResponseDto GetScheduleDetail(Long ScheduleDetailNo) {
        Schedule_detail scheduleDetail = scheduleDetailRepository.findById(ScheduleDetailNo)
                .orElseThrow(() -> new RuntimeException("해당 스케줄은 존재하지 않습니다."));

        return ScheduleDetailResponseDto.from(scheduleDetail);
    }

    @Override
    public List<ScheduleDetailResponseDto> GetScheduleDetailDetail(Long ScheduleNo) {
        if (!scheduleRepository.existsById(ScheduleNo)) {
            throw new RuntimeException("해당 스케줄이 존재하지 않습니다.");
        }

        List<Schedule_detail> selectedDetails = scheduleDetailRepository
                .findSelectedWithCategoryByScheduleNo(ScheduleNo);

        return selectedDetails.stream()
                .map(sd -> ScheduleDetailResponseDto.builder()
                        .scheduleDetailNo(sd.getScheduleDetailNo())
                        .travelCategoryName(sd.getTravelCategory().getName())
                        .isSelected(sd.getIsSelected())
                        .title(sd.getTitle())
                        .content(sd.getContent())
                        .startedAt(sd.getStartedAt())
                        .endedAt(sd.getEndedAt())
                        .createdAt(sd.getCreatedAt())
                        .build())
                .toList();
    }

    @Override
    public String DeleteScheduleDetail(Long ScheduleDetailNo) {
        Schedule_detail sd = scheduleDetailRepository.findById(ScheduleDetailNo)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 스케줄입니다."));

        scheduleDetailRepository.delete(sd);

        return "해당 스케줄을 삭제했습니다.";
    }
}
