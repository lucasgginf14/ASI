package es.udc.asi.bnbria_rest.user.service.dto;

public record StatsAdminView(
  Long totalUsers,
  Long totalProperties,
  Long approvedProperties,
  Long pendingProperties,
  Long totalBookings,
  Long completedBookings,
  Long totalUserReviews,
  Long totalPropertyReviews
) {
}
