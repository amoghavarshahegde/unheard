package com.unheard.service;

import com.unheard.domain.Village;
import com.unheard.dto.BaselineComparisonDto;
import com.unheard.repository.VillageRepository;
import com.unheard.service.engine.BaselineComparator;
import com.unheard.service.engine.ScoringWeights;
import com.unheard.service.engine.VerificationScorer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BaselineService {

    private final VillageRepository villageRepository;
    private final BaselineComparator baselineComparator;
    private final VerificationScorer verificationScorer;
    private final ScoringWeights scoringWeights;

    public BaselineService(VillageRepository villageRepository,
                           BaselineComparator baselineComparator,
                           VerificationScorer verificationScorer,
                           ScoringWeights scoringWeights) {
        this.villageRepository = villageRepository;
        this.baselineComparator = baselineComparator;
        this.verificationScorer = verificationScorer;
        this.scoringWeights = scoringWeights;
    }

    public BaselineComparisonDto compare() {
        List<Village> villages = villageRepository.findAll();
        return baselineComparator.compare(villages, verificationScorer, scoringWeights);
    }
}
