package com.sikdorok.appapi.presentation.admin;

import com.sikdorok.appapi.application.admin.AdminFacade;
import com.sikdorok.appapi.presentation.shared.response.dto.BaseResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Validated
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminFacade adminFacade;

    @GetMapping(path = "/users/find/id")
    public BaseResponse<UUID> usersFindIdByEmail(@RequestParam @NotBlank String email) {
        return BaseResponse.ofSuccess(adminFacade.findIdByEmail(email));
    }

    @PutMapping(path = "/users/password/reset/{usersId}")
    public BaseResponse<Void> usersPasswordReset(@PathVariable UUID usersId) {
        adminFacade.passwordReset(usersId);
        return BaseResponse.ofSuccess();
    }

}
