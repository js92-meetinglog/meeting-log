package org.meetinglog.common.context;

public class UserContext {
  private static final ThreadLocal<CurrentUserData> userThreadLocal = new ThreadLocal<>();

  public static void set(CurrentUserData user) {
    userThreadLocal.set(user);
  }

  public static CurrentUserData get() {
    return userThreadLocal.get();
  }

  public static void clear() {
    userThreadLocal.remove();
  }
}