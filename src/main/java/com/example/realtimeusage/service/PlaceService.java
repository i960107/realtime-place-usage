package com.example.realtimeusage.service;

import com.example.realtimeusage.constant.ErrorCode;
import com.example.realtimeusage.domain.Place;
import com.example.realtimeusage.dto.PlaceDto;
import com.example.realtimeusage.exception.GeneralException;
import com.example.realtimeusage.repository.PlaceRepository;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;

    @Transactional(readOnly = true)
    public List<PlaceDto> getPlaces(Predicate predicate) {
        try {
            return StreamSupport.stream(placeRepository.findAll(predicate).spliterator(), false)
                    .map(PlaceDto::of)
                    .toList();
        } catch (Exception exception) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, exception);
        }
    }

    @Transactional(readOnly = true)
    public Optional<PlaceDto> getPlace(Long placeId) {
        try {
            return placeRepository.findById(placeId)
                    .map(PlaceDto::of);
        } catch (Exception exception) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, exception);
        }
    }

    public boolean createPlace(PlaceDto placeDto) {
        try {
            placeRepository.save(placeDto.toEntity());
            return true;
        } catch (Exception exception) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, exception);
        }
    }

    public boolean modifyPlace(Long placeId, PlaceDto placeDto) {
        // TODO: 2023/11/02 BAD REQUEST VS NOTFOUND 구분해주는게 좋지 않을ㅓ까 
        if (placeId == null || placeDto == null) {
            return false;
        }
        try {
            Optional<Place> optionalPlace = placeRepository.findById(placeId);
            if (optionalPlace.isEmpty()) {
                return false;
            }

            Place place = optionalPlace.get();
            place.update(placeDto);
            placeRepository.save(place);
            return true;
        } catch (Exception exception) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, exception);
        }
    }

    public boolean removePlace(Long placeId) {
        try {
            if (placeId == null) {
                return false;
            }
            Optional<Place> optionalPlace = placeRepository.findById(placeId);
            if (optionalPlace.isEmpty()) {
                return false;
            }
            Place place = optionalPlace.get();
            place.delete();
            placeRepository.save(place);
            return true;
        } catch (Exception exception) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, exception);
        }
    }

    public boolean upsertPlace(PlaceDto dto) {
        try {
            if (dto == null) {
                return false;
            }
            if (dto.id() != null) {
                return createPlace(dto);
            } else {
                return modifyPlace(dto.id(), dto);
            }
        } catch (Exception exception) {
            throw new GeneralException(ErrorCode.DATA_ACCESS_ERROR, exception);
        }
    }
}
