from faker import Faker

# Generate Faker object
# Documentation can be found at: https://faker.readthedocs.io/
fake = Faker()

def main():
    # Open our data insert file, can change later if needed
    open('./data-insert.sql', 'w')

    #TODO: write script to insert 100 users, 200 friendships, 10 groups, 300 messages

if __name__ == '__main__':
    main()