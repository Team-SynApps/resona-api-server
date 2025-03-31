package synapps.resona.api.mysql.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import synapps.resona.api.mysql.member.entity.hobby.GivenHobby;
import synapps.resona.api.mysql.member.entity.hobby.Hobby;
import synapps.resona.api.mysql.member.entity.member_details.MemberDetails;
import synapps.resona.api.mysql.member.exception.HobbyException;
import synapps.resona.api.mysql.member.repository.HobbyRepository;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class HobbyService {
    private final HobbyRepository hobbyRepository;

    @Transactional
    public Hobby registerCustomHobby(MemberDetails memberDetails, String name) {
        Hobby hobby = Hobby.of(memberDetails, name);
        hobbyRepository.save(hobby);
        return hobby;
    }

    @Transactional
    public void registerHobbies(MemberDetails memberDetails, String hobbies) {
        Arrays.stream(hobbies.split(","))
                .map(hobby -> {
                    GivenHobby givenHobby = GivenHobby.of(hobby);
                    return givenHobby.equals(GivenHobby.NOT_GIVEN)
                            ? Hobby.of(memberDetails, hobby)
                            : Hobby.of(memberDetails, givenHobby);
                })
                .forEach(hobbyRepository::save);
    }

    @Transactional
    public Hobby updateHobbyName(Long id, String newName) {
        Hobby hobby = hobbyRepository.findById(id)
                .orElseThrow(HobbyException::hobbyNotFound);
        hobby.updateName(newName);
        return hobby;
    }


    @Transactional
    public void deleteHobby(Long memberDetailsId, String hobbyName) {
        Hobby targetHobby = hobbyRepository.findByMemberDetailsIdAndName(memberDetailsId, hobbyName).orElseThrow(HobbyException::hobbyNotFound);
        targetHobby.softDelete();
    }
}
