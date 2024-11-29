package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.dto.request.interests.InterestsRequest;
import synapps.resona.api.mysql.member.entity.Member;
import synapps.resona.api.mysql.member.entity.interests.Hobby;
import synapps.resona.api.mysql.member.entity.interests.Interests;
import synapps.resona.api.mysql.member.repository.HobbyRepository;

@Service
@RequiredArgsConstructor
public class HobbyService {
    private final HobbyRepository hobbyRepository;

    @Transactional
    public Hobby registerHobby(String name) {
        Hobby hobby = Hobby.of(name);
        hobbyRepository.save(hobby);
        return hobby;
    }

    @Transactional
    public void registerHobbies(String hobbies) {
        String[] hobbiesArray = hobbies.split(",");
        for (String hobby : hobbiesArray) {
            Hobby h = Hobby.of(hobby);
            hobbyRepository.save(h);
        }
    }
}
