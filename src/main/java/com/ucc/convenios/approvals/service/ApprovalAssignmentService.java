package com.ucc.convenios.approvals.service;

import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.repository.RoleRepository;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.enums.ConvenioType;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.ReviewerProfile;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.ReviewerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ApprovalAssignmentService {

    private final RoleRepository roleRepository;
    private final ReviewerProfileRepository reviewerProfileRepository;

    public ApprovalAssignmentService(
            RoleRepository roleRepository,
            ReviewerProfileRepository reviewerProfileRepository
    ) {
        this.roleRepository = roleRepository;
        this.reviewerProfileRepository = reviewerProfileRepository;
    }

    public User assignReviewerForStage(ConvenioStage stage, ConvenioType convenioType) {
        String roleName = getRoleNameForStage(stage, convenioType);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado para la etapa " + stage.name()));

        List<ReviewerProfile> availableReviewers = reviewerProfileRepository.findByRoleAndAvailableTrue(role)
                .stream()
                .filter(profile -> profile.getCurrentActiveCases() < profile.getMaxActiveCases())
                .sorted(Comparator.comparing(ReviewerProfile::getCurrentActiveCases))
                .toList();

        if (availableReviewers.isEmpty()) {
            throw new BadRequestException("No hay revisores disponibles para la etapa " + stage.name() + " con rol " + roleName);
        }

        ReviewerProfile selectedProfile = availableReviewers.get(0);
        selectedProfile.setCurrentActiveCases(selectedProfile.getCurrentActiveCases() + 1);
        reviewerProfileRepository.save(selectedProfile);

        return selectedProfile.getUser();
    }

    public void releaseReviewerCase(User user, ConvenioStage stage) {
        List<String> candidateRoleNames = getCandidateRoleNamesForStage(stage);

        for (String roleName : candidateRoleNames) {
            Role role = roleRepository.findByName(roleName).orElse(null);

            if (role == null) {
                continue;
            }

            List<ReviewerProfile> profiles = reviewerProfileRepository.findByRoleAndAvailableTrue(role)
                    .stream()
                    .filter(profile -> profile.getUser().getId().equals(user.getId()))
                    .toList();

            if (profiles.isEmpty()) {
                continue;
            }

            ReviewerProfile profile = profiles.get(0);

            if (profile.getCurrentActiveCases() > 0) {
                profile.setCurrentActiveCases(profile.getCurrentActiveCases() - 1);
                reviewerProfileRepository.save(profile);
            }

            return;
        }
    }

    private String getRoleNameForStage(ConvenioStage stage, ConvenioType convenioType) {
        if (stage == ConvenioStage.RECTORIA) {
            ConvenioType safeType = convenioType == null ? ConvenioType.MARCO : convenioType;
            return safeType.getRectorRoleName();
        }

        return switch (stage) {
            case PROYECCION -> "GESTOR_PROYECCION";
            case JURIDICA -> "REVISOR_JURIDICO";
            case RECTORIA -> "RECTORIA";
        };
    }

    private List<String> getCandidateRoleNamesForStage(ConvenioStage stage) {
        return switch (stage) {
            case PROYECCION -> List.of("GESTOR_PROYECCION");
            case JURIDICA -> List.of("REVISOR_JURIDICO");
            case RECTORIA -> List.of("RECTORIA", "RECTOR_MEDELLIN");
        };
    }
}