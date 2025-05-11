package ro.ase.ro.api_oncohub_backend.dtos.esmo.neoadjuvant;

public record NeoAdjuvantResponse (
        String initialNeoadjuvantTreatment,
        String followUpNeoadjuvantTreatment,
        String alternativeFollowUpNeoadjuvantTreatment
) {
}
