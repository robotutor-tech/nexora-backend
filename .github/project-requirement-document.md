# Project Requirement Document (Table Format)

| Requirement ID | Description | User Story | Expected Outcomes/Behaviour |
|----------------|-------------|------------|-----------------------------|
| FR-1.1 | User registration and login | As a user, I want to register and log in so that I can access the system securely. | User can create an account and log in with valid credentials. |
| FR-1.2 | Email and mobile verification | As a user, I want to verify my email and mobile so that my account is secure. | User receives verification links/codes and must verify before full access. |
| FR-1.3 | OTP-based password reset and recovery | As a user, I want to reset my password using OTP if I forget it. | User can request OTP, receive it, and reset password securely. |
| FR-1.4 | User profile view and update | As a user, I want to view and update my profile information. | User can see and edit their profile details. |
| FR-1.5 | Credential management | As a user, I want to update my credentials (password, email, etc.). | User can change password/email after authentication. |
| FR-1.6 | Create premises, owner admin, and guest roles | As a user, I want to create premises and assign roles. | User can create premises and assign owner/admin/guest roles. |
| FR-2.1 | OAuth signup and login | As a user, I want to sign up and log in using Google, Facebook, or Github. | User can authenticate via supported OAuth providers. |
| FR-3.1 | View assigned premises | As a user, I want to see all premises assigned to me with pagination and search. | User can view a paginated, searchable list of premises. |
| FR-3.2 | View board health and meta-data | As a user, I want to see board health and meta-data. | User can view board status, serial no, model no, OS version. |
| FR-3.3 | Control actuators via mobile app | As a user, I want to control actuators using WiFi/Bluetooth. | User can operate actuators from the app via WiFi/Bluetooth. |
| FR-3.4 | Operate actuators using push switches | As a user, I want to control actuators using physical switches. | User can trigger actuators with assigned push switches. |
| FR-3.5 | View zones and widgets | As a user, I want to see all zones and widgets with infinite scrolling. | User can scroll through all zones and widgets. |
| FR-3.6 | Operate actuator widgets | As a user, I want to operate actuator widgets as boolean/integer. | User can toggle or set actuator values via widgets. |
| FR-3.7 | View graphical history and usage | As a user, I want to see graphical representations of history and usage. | User can view charts/graphs of device usage and history. |
| FR-4.1 | Invite users to premises | As an owner/admin, I want to invite users to my premises with roles. | Owner/admin can send invites with role assignment. |
| FR-4.2 | Accept/decline invites | As an invited user, I want to accept or decline invites via link. | Invited user can accept/decline within expiry period. |
| FR-4.3 | Remove users (at least one owner) | As an owner/admin, I want to remove users but ensure at least one owner remains. | User can be removed, but system enforces at least one owner. |
| FR-4.4 | Set WiFi credentials with premises ID | As an owner/admin, I want to set WiFi credentials for a premises. | WiFi credentials can be set with premises ID and temp token. |
| FR-4.5 | Add predefined devices/boards | As an owner/admin, I want to add predefined devices/boards. | Devices/boards are added, feeds/widgets/permissions/rules auto-created. |
| FR-4.6 | Add sensors to predefined devices | As an owner/admin, I want to add sensors to open slots. | Sensors can be added to available slots on devices. |
| FR-4.7 | Update permissions for feeds/roles | As an owner/admin, I want to update feed permissions for roles. | Permissions can be updated for feeds and roles. |
| FR-4.8 | Rename feeds and widgets | As an owner/admin, I want to rename feeds and widgets. | Feeds and widgets can be renamed. |
| FR-4.9 | Reassign widgets to zones | As an owner/admin, I want to reassign widgets to different zones. | Widgets can be moved between zones. |
| FR-4.10 | Create custom roles and assign feed access | As an owner/admin, I want to create custom roles and assign feed access. | Custom roles can be created and assigned feed access. |
| FR-4.11 | Remove (deactivate) predefined devices | As an owner/admin, I want to remove predefined devices. | Devices are deactivated, not deleted. |
| FR-4.12 | Add custom boards with custom code | As an owner/admin, I want to add custom boards via APIs. | Custom boards can be added using APIs. |
| FR-5.1 | Audit all major events | As a system, I want to audit all major events. | All major events are logged for security and monitoring. |
| FR-5.2 | Feed value updates and history maintenance | As a system, I want to update feed values and maintain history. | Feed values are updated, and history is maintained for analysis. |
| FR-5.3 | Sensor updates trigger assigned rules | As a system, I want sensor updates to trigger assigned rules. | Assigned rules are triggered automatically on sensor updates. |
| FR-5.4 | Sensor feeds only updatable by board | As a system, I want sensor feeds to be updatable only by the board. | Sensor feeds can only be updated by the associated board. |
| FR-5.5 | Local server syncs records with main server | As a local server, I want to sync records with the main server. | Local server syncs data with the main server for consistency. |
| FR-5.6 | Scheduler triggers at scheduled times | As a system, I want the scheduler to trigger tasks at scheduled times. | Tasks are executed as per the schedule. |
| FR-5.7 | System notifications | As a user, I want to receive system notifications. | Users receive notifications in-app, via voice assistant, and critical ones via call/message. |
| FR-6.1 | Add custom calculation formulas | As an owner/admin, I want to add custom calculation formulas. | Custom calculation formulas can be added and used in the system. |
| FR-7.1 | Rules based on usage history | As an owner/admin, I want to create rules based on usage history. | Rules can be created using usage history within specified time frames. |
| FR-7.2 | Conditions based on trigger source and feed value | As an owner/admin, I want to set conditions based on trigger source and feed value. | Conditions can be defined based on the source of the trigger and the value of the feed. |
| FR-8.1 | Purchase and manage licenses | As a user, I want to purchase and manage licenses. | Users can buy and manage licenses (FREE, BASIC, PRO, ENTERPRISE) through the system. |
| FR-8.2 | License pricing based on premises and device count | As a user, I want to know license pricing based on my premises and device count. | License pricing is displayed based on the number of premises and devices. |
| FR-8.3 | License limits | As a user, I want to be aware of the limits of my license. | The system enforces license limits (e.g., PRO: 10 premises, BASIC/FREE: 1 premises, device/rule/credit limits). |
| FR-8.4 | License expiration and validation | As a user, I want to know about license expiration and validation. | The system validates licenses and notifies users of impending expirations. |

---

## 2. Non-Functional Requirements

| Requirement ID | Description | User Story | Expected Outcomes/Behaviour |
|----------------|-------------|------------|-----------------------------|
| NFR-1 | User interaction tracking | As a product owner, I want to track user interactions (clicks, page visits) to analyze usage patterns. | System logs and reports user interactions for analytics. |
| NFR-2 | Scalability | As a system owner, I want the platform to support 10-100 million daily users across multiple countries and high peak traffic. | System scales horizontally and remains performant under heavy load. |
| NFR-3 | High availability | As a user, I want the system to be available 99.9% of the time. | System is resilient to failures and downtime is minimized. |
| NFR-4 | Performance | As a user, I want the system to respond in less than 1 second for 99% of requests. | System consistently delivers fast response times. |
| NFR-5 | Durability and consistency | As a user, I want my data to be durable and consistent. | Data is never lost and remains consistent across the system. |

---

## 3. Hardware (Board) Requirements

| Requirement ID | Description | User Story | Expected Outcomes/Behaviour |
|----------------|-------------|------------|-----------------------------|
| HW-1 | Setup mode for WiFi credentials and premises ID | As an installer, I want to configure WiFi and premises ID during setup. | Device enters setup mode and accepts WiFi credentials and premises ID. |
| HW-2 | Send sensor/button updates to server | As a device, I want to send sensor and button press updates to the server. | Device transmits updates to the server in real time. |
| HW-3 | Update actuator state from server | As a device, I want to update actuator state based on server feed changes. | Device receives and applies actuator state changes from the server. |
| HW-4 | Support OS version and remote updates | As a device owner, I want to update device OS remotely. | Device supports remote OS updates. |
| HW-5 | Switch between auto/manual modes | As a user, I want the device to switch between automatic and manual modes based on connectivity. | Device switches modes automatically and allows manual override. |
| HW-6 | Map switches to actuators | As a user, I want each switch to control a specific actuator. | Switches are mapped to actuators as configured. |
| HW-7 | Connectivity checks and mode switching | As a device, I want to check internet connectivity every 5 minutes and switch modes accordingly. | Device checks connectivity and switches between manual/automatic modes. |
| HW-8 | Manual mode control via WiFi/Bluetooth | As a user, I want to control the device in manual mode via WiFi or Bluetooth. | Device can be controlled locally in manual mode. |
| HW-9 | Permanent manual mode disables connectivity checks | As a user, I want to set permanent manual mode so the device stops checking connectivity. | Device remains in manual mode and does not check for internet. |
| HW-10 | Manual mode events not recorded on server | As a user, I understand that manual mode events are not recorded on the server. | Manual mode actions are not synced, leading to possible data mismatch. |

---

## 4. Local Server Requirements

| Requirement ID | Description | User Story | Expected Outcomes/Behaviour |
|----------------|-------------|------------|-----------------------------|
| LS-1 | Setup WiFi credentials and premises ID | As an installer, I want to configure WiFi and premises ID for the local server. | Local server accepts WiFi credentials and premises ID in setup mode. |
| LS-2 | Copy and sync essential data | As a local server, I want to copy and sync essential data (scheduler, rules, feeds, widgets, last 90 days of events). | Local server maintains a copy of essential data for offline operation. |
| LS-3 | Sync historic data when internet is restored | As a local server, I want to sync historic data with the main server when internet is restored. | Local server uploads unsynced data to the main server upon reconnection. |
| LS-4 | Support OS version and remote updates | As a local server owner, I want to update the server OS remotely. | Local server supports remote OS updates. |

---

This document outlines the core requirements for the premises automation platform, covering user management, device and premises control, system and hardware integration, and non-functional expectations. Each feature should be implemented with scalability, security, and user experience in mind.
