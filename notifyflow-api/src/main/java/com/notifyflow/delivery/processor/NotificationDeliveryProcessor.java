package com.notifyflow.delivery.processor;

import com.notifyflow.delivery.dispatcher.NotificationDispatcher;
import com.notifyflow.exception.NotificationDeliveryException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Service
public class NotificationDeliveryProcessor {
    private final NotificationClaimService claimService;
    private final NotificationCompletionService completionService;
    private final NotificationDispatcher dispatcher;

    public NotificationDeliveryProcessor(NotificationClaimService claimService, NotificationCompletionService completionService, NotificationDispatcher dispatcher) {
        this.claimService = claimService;
        this.completionService = completionService;
        this.dispatcher = dispatcher;
    }

    public void process(Long notificationId) {
        final ClaimedNotification claimed;

        try {
            claimed = claimService.claim(notificationId);
        } catch (ObjectOptimisticLockingFailureException e) {
            return;
        }

        if (claimed == null) {
            return;
        }

        try {
            dispatcher.dispatch(claimed);
            completionService.markSent(notificationId, claimed.attemptId());
        } catch (NotificationDeliveryException e) {
            completionService.markFailed(notificationId, claimed.attemptId(), safeMessage(e));
        }
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        return message == null || message.isBlank() ? exception.getClass().getSimpleName() : message;
    }
}
