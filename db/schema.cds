namespace com.cengage;

using { User } from '@sap/cds/common';

entity NodeIDAssigner {
    key USER_ID : String(512);
    NODE_ID: Integer;
    createdAt  : Timestamp @cds.on.insert: $now;
    createdBy  : User      @cds.on.insert: $user;
}