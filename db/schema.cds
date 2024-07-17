namespace com.cengage;

using { User } from '@sap/cds/common';

entity NodeIDAssigner {
    @title: 'User:Instance ID' key USER_INSTANCE_ID : String(512);
    @title: 'Node ID'              NODE_ID: Integer not null @assert.notNull: false @assert.range: [192, 255];
    @title: 'Created By'           createdBy  : User      @cds.on.insert: $user;
    @title: 'Created At'           createdAt  : Timestamp @cds.on.insert: $now;
}