from faker import Faker
import random

# Generate Faker object
# Documentation can be found at: https://faker.readthedocs.io/
fake = Faker()
userID = -1
groupID = -1
messageID = -1
friends = set()

def create_profile() -> str:
    # TODO: Make it so that the IDs are random to test trigger
    name = fake.name()
    email = fake.ascii_free_email()
    password = fake.password(length=10, special_chars=True)
    date = fake.date_of_birth(minimum_age=10)
    login = fake.date_time()

    global userID
    userID += 1
    return f'INSERT INTO profile VALUES({userID}, \'{name}\', \'{email}\', \'{password}\', \'{date}\', \'{login}\');\n'

def create_friendship() -> str:
    global userID
    global friends
    friendA = friendB = None

    # Make sure that the friend pairings are unique
    while friendA is None or (friendA, friendB) in friends or (friendB, friendA) in friends:
        friendA, friendB = random.randint(0, userID - 1), random.randint(0, userID - 1)

    friends.add((friendA, friendB))

    date = fake.date()

    # Determine if it should be a blank text or not
    # This has been set to a 70% chance that there is a text
    if random.randint(0, 9) >= 3:
        requestText = fake.text(200).replace('\n', ' ')
        return f'INSERT INTO friend VALUES({friendA}, {friendB}, \'{date}\', \'{requestText}\');\n'
    
    return f'INSERT INTO friend VALUES({friendA}, {friendB}, \'{date}\');\n'

def create_group(min_size: int = 5, max_size: int = 15) -> list[str]:
    """
    Returns a list of SQL commands to both create a group and add members to said group.
    
    :param int min_size: Minimum number of profiles to add to the group. Inclusive.
    :param int max_size: Maximum number of profiles to add to the group. Inclusive.
    """
    global groupID
    global userID
    ret = list()
    num_members = random.randint(min_size, max_size)

    # Generate group
    groupID += 1
    name = fake.company()
    size = random.randint(num_members, max_size * 2)
    description = fake.text(200).replace("\n", " ")
    ret.append(f'INSERT INTO groupInfo VALUES({groupID}, \'{name}\', {size}, \'{description}\');\n')

    # Generate Member Inserts
    group = set()
    for _ in range(num_members):
        user = random.randint(0, userID)
        while user in group:
            user = random.randint(0, userID)
        group.add(user)

        roles = ['manager', 'member']
        ret.append(f'INSERT INTO groupMember VALUES ({groupID}, {user}, \'{random.choice(roles)}\', \'{fake.date_time()}\');\n')

    return ret

def create_message() -> str:
    global userID
    global messageID
    global groupID

    messageID += 1
    fromID = random.randint(0, userID)
    message = fake.text(200).replace("\n", " ")
    toUser = toUser = random.randint(0, userID)
    while toUser == fromID:
        toUser = random.randint(0, userID)
        
    #TODO: Consider message sender having to be in group. Can use hash map or hash set to track this
    toGroup = random.randint(0, groupID)
    timeSent = fake.date_time()
    

    return f'INSERT INTO message VALUES({messageID}, {fromID}, \'{message}\', {toUser}, {toGroup}, \'{timeSent}\');\n'

def main():
    # Open our data insert file, can change later if needed
    with open('./sample-data.sql', 'w') as file:
        #TODO: write script to insert 100 users, 200 friendships, 10 groups, 300 messages
        file.write('-- Sample data file for group 4\'s DBMS Final Project\n\n')

        # Generate Profiles
        file.write('-- Generate 100 Profiles\n')
        for _ in range(100):
            file.write(create_profile())
        file.write('\n')

        # Generate Friendships
        file.write('-- Generate 200 Friendships\n')
        for _ in range(200):
            file.write(create_friendship())
        file.write('\n')

        # Generate Groups
        file.write('-- Generate 10 Groups\n')
        for _ in range(10):
            file.writelines(create_group())
        file.write('\n')

        # Generate Messages
        file.write('-- Generate 300 Messages\n')
        for _ in range(300):
            file.write(create_message())

if __name__ == '__main__':
    main()