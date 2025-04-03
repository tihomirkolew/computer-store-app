package computer_store_app.web;

import computer_store_app.recommendation.client.dto.Recommendation;
import computer_store_app.recommendation.service.RecommendationService;
import computer_store_app.security.AuthenticationMetadata;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.UUID;


@Controller
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping
    public ModelAndView showRecommendationForm(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata) {

        ModelAndView modelAndView = new ModelAndView("recommendation-form"); // View name
        modelAndView.addObject("userEmail", authenticationMetadata.getEmail());

        return modelAndView;
    }

    @PostMapping
    public ModelAndView sendRecommendation(@AuthenticationPrincipal AuthenticationMetadata authenticationMetadata,
                                             @RequestParam("content") String content) {

        String userEmail = authenticationMetadata.getEmail();


        recommendationService.sendRecommendation(userEmail, content);


        ModelAndView modelAndView = new ModelAndView("recommendation-form");
        modelAndView.addObject("userEmail", userEmail);

        return new ModelAndView("recommendation-successful");
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView getAllRecommendations() {

        List<Recommendation> recommendationsList = recommendationService.getAllRecommendations();

        ModelAndView modelAndView = new ModelAndView("recommendations-list");
        modelAndView.addObject( "recommendationsList", recommendationsList);

        return modelAndView;
    }

    @PostMapping("/{id}/archive")
    @PreAuthorize("hasRole('ADMIN')")
    public ModelAndView archiveRecommendation(@PathVariable UUID id) {
        recommendationService.archiveRecommendation(id);

        // Redirect to the recommendations list after archiving
        return new ModelAndView("redirect:/recommendations/list");
    }


}
