package cap.ddg.hack.service;

/**
 * This exception is thrown when the requested team entry is not found.
 *
 */
public class TeamNotFoundException extends RuntimeException {

    public TeamNotFoundException(String id) {
        super(String.format("No team entry found with id: <%s>", id));
    }
}
