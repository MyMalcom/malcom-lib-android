#Stats Module

This module allows you to send two useful statistics from your app: events and tags.

We'll explain what they mean and how easy it is to send the statistics. Then we'll show examples of what you can do with that information.

##How to send stats

###Events

An event represents an action that the user has performed in your app. For example, if you want to track users that enter the "Deals" section of your app, call this method:

```java
MalcomLib.startEvent("enter-deals");
```

You can use whichever name you want. We recommend you to choose short names. They will be easier to manage later.

###Tags

A tag represents a feature that a user enables or disables. A typical usage is when a user subscribes to receive email offers or [notifications](Notifications.md) of some topic. For example, if the user enables an option to receive emails with deals you can call this method:

```java
MalcomLib.addTag("email-deals");
```

When the user disables the option, you should call:

```java
MalcomLib.removeTag("email-deals");
```

As with events, you can choose any names for your tags but we recommend you to use short names.

##Using events and tags

You can use events and tags in many ways. For example, in [Malcom web](http://malcom.mymalcom.com) you can create a segment that includes users that entered the "Deals" section of your app (i.e. that triggered the "enter-deals" event). That segmentation lets you see statistics for that kind of users and also send [notifications](Notifications.md) and [campaigns](Campaigns.md) only to them.
